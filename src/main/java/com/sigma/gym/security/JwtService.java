// src/main/java/com/sigma/gym/security/JwtService.java
package com.sigma.gym.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sigma.gym.config.JwtProperties;
import com.sigma.gym.entity.UserEntity;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserEntity user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("roles", user.getRoles().stream()
                        .map(role -> role.getName()) // Role.getName() = "MEMBER", etc.
                        .collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }

    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey()))
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, UserEntity user) {
        String username = extractUsername(token);
        return username.equals(user.getEmail());
    }
}
