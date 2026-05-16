package com.karthik.urlshortner.url.controller;

import com.karthik.urlshortner.url.dto.CreateShortUrlRequest;
import com.karthik.urlshortner.url.dto.ShortUrlResponse;
import com.karthik.urlshortner.url.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    @PostMapping
    public ShortUrlResponse createShortUrl(@Valid @RequestBody CreateShortUrlRequest request){
        return urlService.createShortUrl(request);
    }
}
