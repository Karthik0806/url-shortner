package com.karthik.urlshortener.analytics.repo;

import com.karthik.urlshortener.analytics.entity.Analytics;
import com.karthik.urlshortener.url.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnalyticsRepo
        extends JpaRepository<Analytics, Long> {

    List<Analytics> findByUrl(
            Url url
    );
}