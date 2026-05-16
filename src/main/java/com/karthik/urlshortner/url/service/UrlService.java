package com.karthik.urlshortner.url.service;

import com.karthik.urlshortner.common.util.Base62Encoder;
import com.karthik.urlshortner.config.RedisConfig;
import com.karthik.urlshortner.url.dto.CreateShortUrlRequest;
import com.karthik.urlshortner.url.dto.ShortUrlResponse;
import com.karthik.urlshortner.url.entity.Url;
import com.karthik.urlshortner.url.repository.UrlRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepo urlRepo;
    private final StringRedisTemplate redisTemplate;

    public ShortUrlResponse createShortUrl(CreateShortUrlRequest request){

        if (request.getCustomAlias()!=null && urlRepo.existsByCustomAlias(request.getCustomAlias())){
            throw new RuntimeException("Custom alias already exists");
        }
        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .expiresAt(request.getExpiresAt())
                .customAlias(request.getCustomAlias())
                .build();
        Url savedUrl = urlRepo.save(url);
        String shortCode;
        if (request.getCustomAlias()!=null && !request.getCustomAlias().isBlank()){
            shortCode = request.getCustomAlias();
        }else {
             shortCode = Base62Encoder.encode(savedUrl.getId());
        }
        savedUrl.setShortCode(shortCode);
        urlRepo.save(savedUrl);
        redisTemplate.opsForValue().set(shortCode, savedUrl.getOriginalUrl());
        return ShortUrlResponse.builder()
                .originalUrl(savedUrl.getOriginalUrl())
                .shortCode(shortCode)
                .shortUrl("https://go.karthiknarravula.dev/"+shortCode)
                .build();
    }
    public String getOriginalUrl(String shortCode) {

        String cachedUrl = redisTemplate.opsForValue().get(shortCode);
        if (cachedUrl != null) {
            Url cachedEntity = urlRepo.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException("Short URL not found"));
            Long currentClicks = cachedEntity.getClickCount() == null ? 0L : cachedEntity.getClickCount();
            cachedEntity.setClickCount(currentClicks + 1);
            urlRepo.save(cachedEntity);
            return cachedUrl;

        }
        Url url = urlRepo.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException("Short URL not found"));
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Short URL has expired");

        }
        Long currentClicks = url.getClickCount() == null ? 0L : url.getClickCount();
        url.setClickCount(currentClicks + 1);
        urlRepo.save(url);
        redisTemplate.opsForValue().set(shortCode, url.getOriginalUrl());
        return url.getOriginalUrl();

    }
}
