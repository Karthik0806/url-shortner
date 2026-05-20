package com.karthik.urlshortener.analytics.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AnalyticsSummaryResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private List<AnalyticsResponse> analytics;
}
