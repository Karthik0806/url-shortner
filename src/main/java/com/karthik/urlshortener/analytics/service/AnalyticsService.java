package com.karthik.urlshortener.analytics.service;

import com.karthik.urlshortener.analytics.dto.AnalyticsResponse;
import com.karthik.urlshortener.analytics.dto.AnalyticsSummaryResponse;
import com.karthik.urlshortener.analytics.entity.Analytics;
import com.karthik.urlshortener.analytics.enums.DeviceType;
import com.karthik.urlshortener.analytics.repo.AnalyticsRepo;

import com.karthik.urlshortener.common.exception.ForbiddenException;
import com.karthik.urlshortener.security.entity.CustomUserDetails;
import com.karthik.urlshortener.url.entity.Url;
import com.karthik.urlshortener.url.repository.UrlRepo;
import com.karthik.urlshortener.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    private final AnalyticsRepo analyticsRepo;
    private final UrlRepo urlRepo;
    private final UserService userService;
    public void saveAnalytics(Url url, HttpServletRequest request){
        Analytics analytics = Analytics.builder().url(url)
                .ipAddress(extractIpAddress(request))
                .browser(extractBrowser(request.getHeader("User-Agent")))
                .device(extractDevice(request.getHeader("User-Agent")))
                .referer(request.getHeader("Referer"))
                .build();

        analyticsRepo.save(analytics);
        log.info("Analytics saved for shortCode: {}", url.getShortCode());
    }

    public AnalyticsSummaryResponse getAnalytics(Url url) {

        List<Analytics> analyticsList =
                analyticsRepo.findByUrl(url);
        List<AnalyticsResponse> analyticsResponses = analyticsList
                        .stream()
                        .map(analytics ->
                                AnalyticsResponse.builder()
                                        .ipAddress(analytics.getIpAddress())
                                        .browser(analytics.getBrowser())
                                        .device(analytics.getDevice())
                                        .referer(analytics.getReferer())
                                        .createdAt(analytics.getCreatedAt())
                                        .build())
                .toList();

        return AnalyticsSummaryResponse.builder()
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .totalClicks(url.getClickCount())
                .analytics(analyticsResponses)
                .build();
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractBrowser(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        }
        if (userAgent.contains("Firefox")) {
            return "Firefox";
        }
        if (userAgent.contains("Safari")) {
            return "Safari";
        }
        if (userAgent.contains("Edge")) {
            return "Edge";
        }
        return "Unknown";
    }

    private DeviceType extractDevice(String userAgent) {
        if (userAgent == null) {
            return DeviceType.UNKNOWN;
        }
        String lower = userAgent.toLowerCase();
        if (lower.contains("mobile")) {
            return DeviceType.MOBILE;
        }
        if (lower.contains("tablet")) {
            return DeviceType.TABLET;
        }

        return DeviceType.DESKTOP;
    }
}