package com.sigma.gym.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;


@Service
public class JwtService {
    @Value("${application.security.jwt.secretKey}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        try {
            return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey())
                .compact();
        } catch (Exception error) {
            throw new RuntimeException("[JwtService.generateToken] -> " + error.getMessage(), error);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractClaim(token, Claims::getSubject);
            return (username.equals(userDetails.getUsername()));
        } catch (Exception error) {
            throw new RuntimeException("[JwtService.isTokenValid] -> " + error.getMessage(), error);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception error) {
            throw new RuntimeException("[JwtService.isTokenExpired] -> " + error.getMessage(), error);
        }
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception error) {
            throw new RuntimeException("[JwtService.extractUsername] -> " + error.getMessage(), error);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts
                    .parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (Exception error) {
            throw new RuntimeException("[JwtService.extractClaim] -> " + error.getMessage(), error);
        }
    }

    private SecretKey getSecretKey() {
        try {
            return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception error) {
            throw new RuntimeException("[JwtService.getSecretKey] -> " + error.getMessage(), error);
        }
    }
}// src/main/java/com/sigma/gym/security/JwtService.java
