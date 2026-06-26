package com.karthik.urlshortener.auth.controller;

import com.karthik.urlshortener.auth.dto.*;
import com.karthik.urlshortener.auth.service.AuthService;
import com.karthik.urlshortener.auth.service.GoogleAuthService;
import com.karthik.urlshortener.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(authService.refreshToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestBody RefreshTokenRequest request) {

        authService.logout(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Logout successful")
                .data("Logged out successfully")
                .build();
    }
    @PostMapping("/google")
    public ApiResponse<AuthResponse> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request
    ) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Google login successful")
                .data(googleAuthService.authenticate(request.getIdToken()))
                .build();
    }
}