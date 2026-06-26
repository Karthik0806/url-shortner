package com.karthik.urlshortener.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.karthik.urlshortener.auth.dto.AuthResponse;
import com.karthik.urlshortener.auth.entity.RefreshToken;
import com.karthik.urlshortener.common.exception.InvalidTokenException;
import com.karthik.urlshortener.security.service.JwtService;
import com.karthik.urlshortener.user.entity.User;
import com.karthik.urlshortener.user.enums.Role;
import com.karthik.urlshortener.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    @Value("${google.client-id}")
    private String googleClientId;

    public AuthResponse authenticate(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance())
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new InvalidTokenException("Invalid Google token");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String username = (String) payload.get("name");
            User user = userRepo.findByEmail(email).orElseGet(() -> {
                        User newUser = User.builder()
                                .email(email)
                                .username(username)
                                .password(UUID.randomUUID().toString())
                                .role(Role.USER)
                                .build();

                        return userRepo.save(newUser);
                    });

            String accessToken = jwtService.generateAccessToken(email);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
        } catch (Exception e) {
            throw new InvalidTokenException("Google authentication failed");
        }
    }
}