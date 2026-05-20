package com.karthik.urlshortener.url.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortUrlResponse {
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
}
