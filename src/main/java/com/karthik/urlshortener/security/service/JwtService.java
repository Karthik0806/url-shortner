package com.karthik.urlshortener.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    private String buildToken(String email,long expiration){
        return Jwts.builder().setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date( System.currentTimeMillis()+expiration)).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }
    public String generateAccessToken(String email){
        return buildToken(email,accessTokenExpiration);
    }
    public String generateRefreshToken(String email){
        return buildToken(email,refreshTokenExpiration);
    }

    public String extractEmail(String token){
        return extractClaims(token).getSubject();

    }
    public boolean isTokenValid(String token){
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
