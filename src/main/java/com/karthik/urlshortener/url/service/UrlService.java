package com.karthik.urlshortener.url.service;

import com.karthik.urlshortener.common.config.AppProperties;
import com.karthik.urlshortener.common.exception.*;
import com.karthik.urlshortener.common.util.Base62Encoder;
import com.karthik.urlshortener.security.entity.CustomUserDetails;
import com.karthik.urlshortener.url.dto.*;
import com.karthik.urlshortener.url.entity.Url;
import com.karthik.urlshortener.url.repository.UrlRepo;
import com.karthik.urlshortener.user.entity.User;
import com.karthik.urlshortener.user.repository.UserRepo;
import com.karthik.urlshortener.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private static final Set<String> RESERVED_ALIASES = Set.of(
            "api",
            "app",
            "login",
            "register",
            "dashboard",
            "admin"
    );

    private static final Duration CACHE_TTL =
            Duration.ofHours(24);

    private final UrlRepo urlRepo;

    private final UserService userService;

    private final RedisTemplate<String, CachedUrl>
            redisTemplate;

    private final AppProperties appProperties;
    private final UserRepo userRepo;

    @Transactional
    public ShortUrlResponse createShortUrl(
            CreateShortUrlRequest request
    ) {

        // STEP 1: Get authenticated user
        final User user =
                userService.getCurrentUserEntity();

        log.info(
                "Creating short URL for user: {}",
                user.getEmail()
        );

        String customAlias = null;

        // STEP 2: Normalize custom alias
        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isBlank()) {

            customAlias = request
                    .getCustomAlias()
                    .trim()
                    .toLowerCase();

            // STEP 3: Reserved alias validation
            if (RESERVED_ALIASES.contains(customAlias)) {

                throw new ReservedAliasException(
                        "Reserved alias cannot be used"
                );
            }

            // STEP 4: Alias uniqueness validation
            if (urlRepo.existsByCustomAlias(customAlias)) {

                log.warn(
                        "Custom alias already exists: {}",
                        customAlias
                );

                throw new DuplicateAliasException(
                        "Custom alias already exists"
                );
            }
        }

        // STEP 5: Expiration validation
        if (request.getExpiresAt() != null &&
                request.getExpiresAt()
                        .isBefore(Instant.now())) {

            throw new InvalidExpirationException(
                    "Expiration date cannot be in the past"
            );
        }

        // STEP 6: Duplicate URL detection
        if (customAlias == null) {

            final Url existingUrl =
                    urlRepo.findByOriginalUrlAndUser(
                                    request.getOriginalUrl(),
                                    user
                            )
                            .orElse(null);

            if (existingUrl != null &&
                    (existingUrl.getExpiresAt() == null ||
                            existingUrl.getExpiresAt()
                                    .isAfter(Instant.now()))) {

                log.info(
                        "Duplicate URL detected for user: {}",
                        user.getEmail()
                );

                return buildShortUrlResponse(
                        existingUrl
                );
            }
        }

        // STEP 7: Create URL entity
        final Url url = Url.builder()

                .user(user)

                .originalUrl(
                        request.getOriginalUrl()
                )

                .expiresAt(
                        request.getExpiresAt()
                )

                .customAlias(customAlias)

                .build();

        // STEP 8: Save first to generate ID
        final Url savedUrl =
                urlRepo.save(url);

        // STEP 9: Generate short code
        final String shortCode;

        if (customAlias != null) {

            shortCode = customAlias;

        } else {

            shortCode = Base62Encoder.encode(
                    savedUrl.getId()
            );
        }

        // STEP 10: Update short code
        savedUrl.setShortCode(shortCode);

        // STEP 11: Save updated entity
        urlRepo.save(savedUrl);

        log.info(
                "Short URL created successfully: {}",
                shortCode
        );

        // STEP 12: Cache in Redis
        cacheUrl(savedUrl);

        log.info(
                "Short URL created with code: {}",
                shortCode
        );
        // STEP 13: Return response
        return buildShortUrlResponse(
                savedUrl
        );
    }

    public Url getOriginalUrl(
            String shortCode
    ) {

        // STEP 1: Check Redis cache
        final CachedUrl cachedUrl =
                redisTemplate
                        .opsForValue()
                        .get(shortCode);

        // STEP 2: Return cached result if present
        if (cachedUrl != null) {

            validateExpiration(
                    cachedUrl.getExpiresAt()
            );

            urlRepo.incrementClickCount(
                    cachedUrl.getUrlId()
            );

            log.info(
                    "Cache hit for shortCode: {}",
                    shortCode
            );

            return Url.builder()

                    .id(cachedUrl.getUrlId())

                    .shortCode(shortCode)

                    .originalUrl(
                            cachedUrl.getOriginalUrl()
                    )

                    .expiresAt(
                            cachedUrl.getExpiresAt()
                    )

                    .build();
        }

        log.info(
                "Cache miss for shortCode: {}",
                shortCode
        );

        // STEP 3: Database fallback
        final Url url =
                urlRepo.findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new UrlNotFoundException(
                                        "Short URL not found"
                                )
                        );

        // STEP 4: Validate expiration
        validateExpiration(url);

        // STEP 5: Atomic click increment
        urlRepo.incrementClickCount(
                url.getId()
        );

        // STEP 6: Cache result
        cacheUrl(url);

        return url;
    }

    public Url getOwnedUrlByShortCode(
            String shortCode
    ) {

        final User currentUser =
                userService.getCurrentUserEntity();

        final Url url =
                urlRepo.findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new UrlNotFoundException(
                                        "Short URL not found"
                                )
                        );

        if (!url.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    "You do not own this URL"
            );
        }

        return url;
    }

    public PaginatedResponse<UserUrlResponse>
    getMyUrls(
            int page,
            int size
    ) {

        CustomUserDetails currentUser =
                userService.getAuthenticatedUser();

        User user = userRepo
                .findById(currentUser.getId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Url> urlPage =
                urlRepo.findByUserOrderByCreatedAtDesc(
                        user,
                        pageable
                );

        List<UserUrlResponse> responses =
                urlPage.getContent()
                        .stream()
                        .map(url ->
                                UserUrlResponse.builder()

                                        .shortCode(
                                                url.getShortCode()
                                        )

                                        .shortUrl(
                                                "https://go.karthiknarravula.dev/r/"
                                                        + url.getShortCode()
                                        )

                                        .originalUrl(
                                                url.getOriginalUrl()
                                        )

                                        .clickCount(
                                                url.getClickCount()
                                        )

                                        .createdAt(
                                                url.getCreatedAt()
                                        )

                                        .build()
                        )
                        .toList();

        return PaginatedResponse
                .<UserUrlResponse>builder()

                .content(responses)

                .page(
                        urlPage.getNumber()
                )

                .size(
                        urlPage.getSize()
                )

                .totalElements(
                        urlPage.getTotalElements()
                )

                .totalPages(
                        urlPage.getTotalPages()
                )

                .last(
                        urlPage.isLast()
                )

                .build();
    }

    private void cacheUrl(
            Url url
    ) {

        CachedUrl cachedUrl =

                CachedUrl.builder()

                        .urlId(url.getId())

                        .originalUrl(
                                url.getOriginalUrl()
                        )

                        .expiresAt(
                                url.getExpiresAt()
                        )

                        .build();

        redisTemplate
                .opsForValue()
                .set(
                        url.getShortCode(),
                        cachedUrl,
                        CACHE_TTL
                );
    }

    private void validateExpiration(
            Url url
    ) {

        validateExpiration(
                url.getExpiresAt()
        );
    }

    private void validateExpiration(
            Instant expiresAt
    ) {

        if (expiresAt != null &&
                expiresAt.isBefore(Instant.now())) {

            throw new UrlExpiredException(
                    "Short URL has expired"
            );
        }
    }

    private ShortUrlResponse buildShortUrlResponse(
            Url url
    ) {

        return ShortUrlResponse.builder()

                .originalUrl(
                        url.getOriginalUrl()
                )

                .shortCode(
                        url.getShortCode()
                )

                .shortUrl(
                        appProperties.getBaseShortUrl()
                                + url.getShortCode()
                )

                .build();
    }

    private UserUrlResponse buildUserUrlResponse(
            Url url
    ) {

        return UserUrlResponse.builder()

                .originalUrl(
                        url.getOriginalUrl()
                )

                .shortCode(
                        url.getShortCode()
                )

                .shortUrl(
                        appProperties.getBaseShortUrl()
                                + url.getShortCode()
                )

                .clickCount(
                        url.getClickCount()
                )

                .createdAt(
                        url.getCreatedAt()
                )

                .build();
    }
}