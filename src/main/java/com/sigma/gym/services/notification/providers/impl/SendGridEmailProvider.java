package com.sigma.gym.services.notification.providers.impl;

import com.sigma.gym.services.notification.providers.EmailProvider;
import com.sigma.gym.services.notification.providers.NotificationProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SendGrid email provider implementation
 */
@Service
@ConditionalOnProperty(name = "notifications.email.provider", havingValue = "SENDGRID")
@Slf4j
public class SendGridEmailProvider implements EmailProvider {
    
    private final String apiKey;
    private final String fromEmail;
    private final RestTemplate restTemplate;
    
    public SendGridEmailProvider(
            @Value("${sendgrid.api.key:}") String apiKey,
            @Value("${notifications.email.from}") String fromEmail,
            RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String send(String to, String subject, String htmlBody, Map<String, Object> metadata) {
        if (!isConfigured()) {
            throw new NotificationProviderException("SendGrid", "Provider not configured - missing API key");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            Map<String, Object> payload = buildSendGridPayload(to, subject, htmlBody, metadata);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.sendgrid.com/v3/mail/send", 
                entity, 
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully via SendGrid to: {}", to);
                return "SUCCESS: " + response.getBody();
            } else {
                throw new NotificationProviderException("SendGrid", 
                    "HTTP " + response.getStatusCode() + ": " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Failed to send email via SendGrid to: {}", to, e);
            throw new NotificationProviderException("SendGrid", "Failed to send email: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> buildSendGridPayload(String to, String subject, String htmlBody, Map<String, Object> metadata) {
        Map<String, Object> payload = new HashMap<>();
        
        // From
        Map<String, String> from = Map.of("email", fromEmail, "name", "Sigma Gym");
        payload.put("from", from);
        
        // To
        Map<String, String> toAddress = Map.of("email", to);
        Map<String, Object> personalization = Map.of("to", List.of(toAddress));
        List<Map<String, Object>> personalizations = List.of(personalization);
        payload.put("personalizations", personalizations);
        
        // Subject
        payload.put("subject", subject);
        
        // Content
        List<Map<String, String>> content = List.of(
            Map.of("type", "text/html", "value", htmlBody)
        );
        payload.put("content", content);
        
        // Custom args for tracking
        if (metadata != null && !metadata.isEmpty()) {
            payload.put("custom_args", metadata);
        }
        
        return payload;
    }
    
    @Override
    public String getProviderName() {
        return "SendGrid";
    }
    
    @Override
    public boolean isConfigured() {
        return StringUtils.hasText(apiKey) && StringUtils.hasText(fromEmail);
    }
}
