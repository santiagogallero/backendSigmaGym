// src/main/java/com/sigma/gym/config/JwtProperties.java
package com.sigma.gym.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    private String secretKey;
    private long expiration;

    public JwtProperties() {
        // Constructor vacío requerido por Spring
    }

    public JwtProperties(String secretKey, long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}