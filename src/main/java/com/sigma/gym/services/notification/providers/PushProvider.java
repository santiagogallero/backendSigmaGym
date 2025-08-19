package com.sigma.gym.services.notification.providers;

import java.util.List;
import java.util.Map;

/**
 * Push notification provider interface
 */
public interface PushProvider {
    
    /**
     * Send push notification to multiple device tokens
     * @param deviceTokens list of FCM device tokens
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return provider response
     * @throws NotificationProviderException if sending fails
     */
    String send(List<String> deviceTokens, String title, String body, Map<String, Object> data);
    
    /**
     * Send push notification to a single device token
     * @param deviceToken FCM device token
     * @param title notification title
     * @param body notification body
     * @param data additional data payload
     * @return provider response
     * @throws NotificationProviderException if sending fails
     */
    String send(String deviceToken, String title, String body, Map<String, Object> data);
    
    /**
     * Get provider name
     */
    String getProviderName();
    
    /**
     * Check if provider is configured and ready
     */
    boolean isConfigured();
}
