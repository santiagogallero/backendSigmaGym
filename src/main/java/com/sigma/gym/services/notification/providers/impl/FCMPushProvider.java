package com.sigma.gym.services.notification.providers.impl;

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
 * Firebase Cloud Messaging (FCM) push notification provider
 */
@Service
@ConditionalOnProperty(name = "notifications.push.provider", havingValue = "FCM")
@Slf4j
public class FCMPushProvider implements com.sigma.gym.services.notification.providers.PushProvider {
    
    private final String serverKey;
    private final RestTemplate restTemplate;
    
    public FCMPushProvider(
            @Value("${fcm.server.key:}") String serverKey,
            RestTemplate restTemplate) {
        this.serverKey = serverKey;
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String send(String deviceToken, String title, String body, Map<String, Object> data) {
        if (!isConfigured()) {
            throw new NotificationProviderException("FCM", "Provider not configured - missing server key");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "key=" + serverKey);
            
            Map<String, Object> payload = buildFCMPayload(deviceToken, title, body, data);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://fcm.googleapis.com/fcm/send",
                entity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                if (responseBody != null && !responseBody.isEmpty()) {
                    log.info("Push notification sent successfully via FCM to token: {}", 
                        deviceToken.substring(0, Math.min(20, deviceToken.length())) + "...");
                    return "SUCCESS: " + responseBody;
                } else {
                    throw new NotificationProviderException("FCM", "Empty response from FCM");
                }
            } else {
                throw new NotificationProviderException("FCM", 
                    "HTTP " + response.getStatusCode() + ": " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Failed to send push notification via FCM to token: {}", 
                deviceToken.substring(0, Math.min(20, deviceToken.length())) + "...", e);
            throw new NotificationProviderException("FCM", "Failed to send push notification: " + e.getMessage(), e);
        }
    }
    
    @Override  
    public String send(List<String> deviceTokens, String title, String body, Map<String, Object> data) {
        if (deviceTokens == null || deviceTokens.isEmpty()) {
            return "No device tokens provided";
        }
        
        if (!isConfigured()) {
            throw new NotificationProviderException("FCM", "Provider not configured - missing server key");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "key=" + serverKey);
            
            Map<String, Object> payload = buildBatchFCMPayload(deviceTokens, title, body, data);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://fcm.googleapis.com/fcm/send",
                entity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                if (responseBody != null && !responseBody.isEmpty()) {
                    log.info("Batch push notification sent via FCM to {} tokens", deviceTokens.size());
                    return "SUCCESS: " + responseBody;
                } else {
                    throw new NotificationProviderException("FCM", "Empty response from FCM");
                }
            } else {
                throw new NotificationProviderException("FCM", 
                    "HTTP " + response.getStatusCode() + ": " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Failed to send batch push notifications via FCM to {} tokens", deviceTokens.size(), e);
            throw new NotificationProviderException("FCM", "Failed to send batch push notifications: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> buildFCMPayload(String deviceToken, String title, String body, 
                                               Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", deviceToken);
        
        // Notification payload (shows in notification tray)
        Map<String, String> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);
        notification.put("sound", "default");
        notification.put("icon", "ic_notification");
        payload.put("notification", notification);
        
        // Data payload (custom data for app processing)
        if (data != null && !data.isEmpty()) {
            payload.put("data", data);
        }
        
        // High priority for immediate delivery
        payload.put("priority", "high");
        
        return payload;
    }
    
    private Map<String, Object> buildBatchFCMPayload(List<String> deviceTokens, String title, String body, 
                                                    Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("registration_ids", deviceTokens);
        
        // Notification payload
        Map<String, String> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);
        notification.put("sound", "default");
        notification.put("icon", "ic_notification");
        payload.put("notification", notification);
        
        // Data payload
        if (data != null && !data.isEmpty()) {
            payload.put("data", data);
        }
        
        // High priority
        payload.put("priority", "high");
        
        return payload;
    }
    
    @SuppressWarnings("unchecked")
    private String extractFCMError(Map<String, Object> responseBody) {
        if (responseBody == null) {
            return "Unknown error";
        }
        
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");
        if (results != null && !results.isEmpty()) {
            Map<String, Object> result = results.get(0);
            String error = (String) result.get("error");
            if (error != null) {
                return error;
            }
        }
        
        return responseBody.toString();
    }
    
    @Override
    public String getProviderName() {
        return "Firebase Cloud Messaging";
    }
    
    @Override
    public boolean isConfigured() {
        return StringUtils.hasText(serverKey);
    }
}
