package com.karthik.urlshortener.url.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateShortUrlRequest {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?://).+$", message = "Invalid URL format")
    private String originalUrl;
    private Instant expiresAt;
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$",message = "Alias can only contain letters, numbers, underscores and hyphens" )
    private String customAlias;
}
