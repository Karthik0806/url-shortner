package com.karthik.urlshortener.url.controller;

import com.karthik.urlshortener.common.dto.ApiResponse;
import com.karthik.urlshortener.url.dto.CreateShortUrlRequest;
import com.karthik.urlshortener.url.dto.PaginatedResponse;
import com.karthik.urlshortener.url.dto.ShortUrlResponse;
import com.karthik.urlshortener.url.dto.UserUrlResponse;
import com.karthik.urlshortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    @GetMapping("/my")
    public ApiResponse<PaginatedResponse<UserUrlResponse>> getMyUrls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PaginatedResponse<UserUrlResponse>>builder()
                .success(true)
                .message("URLs fetched successfully")
                .data(urlService.getMyUrls(page, size))
                .build();
    }

    @PostMapping
    public ApiResponse<ShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        return ApiResponse.<ShortUrlResponse>builder().success(true).message("Short URL created successfully").data(urlService.createShortUrl(request)).build();
    }
}