package com.sigma.gym.services.notification.providers.impl;

import com.sigma.gym.services.notification.providers.NotificationProviderException;
import com.sigma.gym.services.notification.providers.WhatsappProvider;
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
 * Meta WhatsApp Business API provider
 */
@Service
@ConditionalOnProperty(name = "notifications.whatsapp.provider", havingValue = "META")
@Slf4j
public class MetaWhatsAppProvider implements WhatsappProvider {
    
    private final String accessToken;
    private final String phoneNumberId;
    private final RestTemplate restTemplate;
    
    public MetaWhatsAppProvider(
            @Value("${whatsapp.access.token:}") String accessToken,
            @Value("${whatsapp.phone.number.id:}") String phoneNumberId,
            RestTemplate restTemplate) {
        this.accessToken = accessToken;
        this.phoneNumberId = phoneNumberId;
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String send(String phoneE164, String templateName, Map<String, Object> variables, Map<String, Object> metadata) {
        if (!isConfigured()) {
            throw new NotificationProviderException("WhatsApp", 
                "Provider not configured - missing access token or phone number ID");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            
            Map<String, Object> payload = buildTemplatePayload(phoneE164, templateName, variables);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            String url = String.format("https://graph.facebook.com/v18.0/%s/messages", phoneNumberId);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                url, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                if (responseBody != null && responseBody.containsKey("messages")) {
                    log.info("WhatsApp template message sent successfully to: {}", phoneE164);
                    return "SUCCESS: " + responseBody.toString();
                } else {
                    throw new NotificationProviderException("WhatsApp", 
                        "Unexpected response format: " + responseBody);
                }
            } else {
                throw new NotificationProviderException("WhatsApp", 
                    "HTTP " + response.getStatusCode() + ": " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Failed to send WhatsApp template message to: {}", phoneE164, e);
            throw new NotificationProviderException("WhatsApp", 
                "Failed to send template message: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> buildTemplatePayload(String phoneNumber, String templateName, 
                                                   Map<String, Object> variables) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", phoneNumber);
        payload.put("type", "template");
        
        // Template object
        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", "es")); // Default to Spanish
        
        // Parameters if provided
        if (variables != null && !variables.isEmpty()) {
            Object[] parameterArray = variables.entrySet().stream()
                .map(entry -> Map.of("type", "text", "text", entry.getValue().toString()))
                .toArray();
                
            template.put("components", new Object[] {
                Map.of("type", "body", "parameters", parameterArray)
            });
        }
        
        payload.put("template", template);
        
        return payload;
    }
    
    @Override
    public String getProviderName() {
        return "Meta WhatsApp Business API";
    }
    
    @Override
    public boolean isConfigured() {
        return StringUtils.hasText(accessToken) && StringUtils.hasText(phoneNumberId);
    }
}
