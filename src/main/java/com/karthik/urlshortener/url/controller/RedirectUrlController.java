package com.karthik.urlshortener.url.controller;

import com.karthik.urlshortener.analytics.service.AnalyticsService;
import com.karthik.urlshortener.common.exception.RateLimitExceededException;
import com.karthik.urlshortener.common.service.RateLimitService;
import com.karthik.urlshortener.url.entity.Url;
import com.karthik.urlshortener.url.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectUrlController {

    private final UrlService urlService;
    private final AnalyticsService analyticsService;
    private final RateLimitService rateLimitService;

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();

        boolean allowed = rateLimitService.isAllowed(ipAddress);

        if (!allowed) {
            throw new RateLimitExceededException(
                    "Too many requests. Please try again later");
        }

        Url url = urlService.getOriginalUrl(shortCode);

        analyticsService.saveAnalytics(url, request);
        log.info(
                "Redirecting shortCode: {}",
                shortCode
        );

        return ResponseEntity.status(302)
                .header("Location", url.getOriginalUrl())
                .build();
    }
}