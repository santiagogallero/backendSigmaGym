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
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
    
    // Constructor por defecto
    public MercadoPagoProperties() {
        this.baseUrl = "http://localhost:8080";
        this.notificationUrl = this.baseUrl + "/api/webhook/mercadopago";
        this.successUrl = this.baseUrl + "/payment/success";
        this.failureUrl = this.baseUrl + "/payment/failure";
        this.pendingUrl = this.baseUrl + "/payment/pending";
    }
    
    public String getNotificationUrl() {
        if (notificationUrl == null) {
            notificationUrl = baseUrl + "/api/webhook/mercadopago";
        }
        return notificationUrl;
    }
}
