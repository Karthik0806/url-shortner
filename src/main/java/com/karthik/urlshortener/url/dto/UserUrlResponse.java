package com.karthik.urlshortener.url.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class UserUrlResponse {
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private Long clickCount;
    private Instant createdAt;
}
