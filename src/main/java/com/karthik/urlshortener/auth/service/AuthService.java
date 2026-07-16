package com.karthik.urlshortener.auth.service;

import com.karthik.urlshortener.auth.dto.AuthResponse;
import com.karthik.urlshortener.auth.dto.LoginRequest;
import com.karthik.urlshortener.auth.dto.RefreshTokenRequest;
import com.karthik.urlshortener.auth.dto.RegisterRequest;
import com.karthik.urlshortener.auth.entity.RefreshToken;
import com.karthik.urlshortener.common.exception.InvalidCredentialsException;
import com.karthik.urlshortener.common.exception.UserAlreadyExistsException;
import com.karthik.urlshortener.security.service.JwtService;
import com.karthik.urlshortener.user.entity.User;
import com.karthik.urlshortener.user.enums.Role;
import com.karthik.urlshortener.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request){

        if (userRepo.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        User savedUser = userRepo.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);
        return  AuthResponse.builder().accessToken(jwtService.generateAccessToken(savedUser.getEmail()))
                .refreshToken(refreshToken.getToken())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .build();
    }
    public AuthResponse login(LoginRequest request){
        log.info("Login attempt for email: {}", request.getEmail());
        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(()-> new InvalidCredentialsException("Invalid credentials"));
        boolean passwordMatches =passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches){
            log.warn("Invalid login attempt for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }
        log.info("User logged in successfully: {}", user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user.getEmail()))
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();

    }
    public AuthResponse refreshToken(RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
    public void logout(RefreshTokenRequest request){
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
        log.info("User logged out successfully");
    }
}
