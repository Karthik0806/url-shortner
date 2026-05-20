package com.karthik.urlshortener.url.repository;

import com.karthik.urlshortener.url.entity.Url;
import com.karthik.urlshortener.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<Url,Long> {

    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);

    Optional<Url> findByCustomAlias(String customAlias);
    boolean existsByCustomAlias(String customAlias);

    Optional<Url> findByOriginalUrl(String originalUrl);
    Page<Url> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    Optional<Url> findByOriginalUrlAndUser(String originalUrl,User user);

    @Transactional
    @Modifying
    @Query("""
       UPDATE Url u
       SET u.clickCount = u.clickCount + 1
       WHERE u.id = :urlId
       """)
    void incrementClickCount(
            @Param("urlId") Long urlId
    );
    Optional<Url> findByShortCodeAndUserId(
            String shortCode,
            Long userId
    );
}
