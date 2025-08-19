package com.sigma.gym.services.notification.providers;

import java.util.Map;

/**
 * Email provider interface for sending email notifications
 */
public interface EmailProvider {
    
    /**
     * Send an email
     * @param to recipient email address
     * @param subject email subject
     * @param htmlBody email body in HTML format
     * @param metadata additional metadata for tracking
     * @return provider response
     * @throws NotificationProviderException if sending fails
     */
    String send(String to, String subject, String htmlBody, Map<String, Object> metadata);
    
    /**
     * Get provider name
     */
    String getProviderName();
    
    /**
     * Check if provider is configured and ready
     */
    boolean isConfigured();
}
