package com.karthik.urlshortener.auth.service;

import com.karthik.urlshortener.auth.entity.RefreshToken;
import com.karthik.urlshortener.auth.repo.RefreshTokenRepo;
import com.karthik.urlshortener.common.exception.InvalidTokenException;
import com.karthik.urlshortener.common.exception.RefreshTokenExpiredException;
import com.karthik.urlshortener.security.service.JwtService;
import com.karthik.urlshortener.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtService jwtService;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshToken createRefreshToken(User user){
        refreshTokenRepo.deleteByUser(user);
        String token = jwtService.generateRefreshToken(user.getEmail());
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        log.info("Refresh token created for user: {}", user.getEmail());
        return refreshTokenRepo.save(refreshToken);
    }
    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(()->new InvalidTokenException("Invalid refresh token"));

        if (!jwtService.isTokenValid(token)){
            throw new InvalidTokenException("Invalid refresh token");
        }
        if (refreshToken.getExpiryDate().isBefore(Instant.now())){
            log.warn(
                    "Refresh token expired for user: {}",
                    refreshToken.getUser().getEmail()
            );
            refreshTokenRepo.delete(refreshToken);
            throw new RefreshTokenExpiredException("Refresh token expired");
        }
        return refreshToken;
    }
    public void deleteRefreshToken(String token){
        refreshTokenRepo.deleteByToken(token);
        log.info("Refresh token deleted successfully");
    }
}
