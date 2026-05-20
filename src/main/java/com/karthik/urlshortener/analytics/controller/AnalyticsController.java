package com.karthik.urlshortener.analytics.controller;

import com.karthik.urlshortener.analytics.dto.AnalyticsSummaryResponse;
import com.karthik.urlshortener.analytics.service.AnalyticsService;
import com.karthik.urlshortener.common.dto.ApiResponse;
import com.karthik.urlshortener.url.entity.Url;
import com.karthik.urlshortener.url.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UrlService urlService;

    @GetMapping("/{shortCode}")
    public ApiResponse<AnalyticsSummaryResponse> getAnalytics(
            @PathVariable String shortCode) {

        Url url = urlService.getOwnedUrlByShortCode(shortCode);

        return ApiResponse.<AnalyticsSummaryResponse>builder()
                .success(true)
                .message("Analytics fetched successfully")
                .data(analyticsService.getAnalytics(url))
                .build();
    }
}