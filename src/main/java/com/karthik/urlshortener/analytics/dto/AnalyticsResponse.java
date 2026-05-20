package com.karthik.urlshortener.analytics.dto;

import com.karthik.urlshortener.analytics.enums.DeviceType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class AnalyticsResponse {

    private String ipAddress;
    private String browser;
    private DeviceType device;
    private String referer;
    private Instant createdAt;
}
