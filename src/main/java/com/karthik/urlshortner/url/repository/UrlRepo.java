package com.karthik.urlshortner.url.repository;

import com.karthik.urlshortner.url.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<Url,Long> {

    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);

    Optional<Url> findByCustomAlias(String customAlias);
    boolean existsByCustomAlias(String customAlias);

}
