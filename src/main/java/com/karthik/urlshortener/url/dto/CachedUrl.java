package com.karthik.urlshortener.url.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CachedUrl implements Serializable {
    private Long urlId;
    private String originalUrl;
    private Instant expiresAt;
}
