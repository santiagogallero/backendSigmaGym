package com.sigma.gym.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.mercadopago")
@Data
public class MercadoPagoProperties {
    
    private String accessToken;
    private String publicKey;
    private String webhookSecret;
    private String baseUrl;
    private String notificationUrl;
    
    // Constructor por defecto
    public MercadoPagoProperties() {
        this.baseUrl = "http://localhost:8080";
        this.notificationUrl = this.baseUrl + "/api/webhook/mercadopago";
    }
    
    public String getNotificationUrl() {
        if (notificationUrl == null) {
            notificationUrl = baseUrl + "/api/webhook/mercadopago";
        }
        return notificationUrl;
    }
}
