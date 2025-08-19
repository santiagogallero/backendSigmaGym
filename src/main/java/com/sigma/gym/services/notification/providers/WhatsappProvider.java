package com.sigma.gym.services.notification.providers;

import java.util.Map;

/**
 * WhatsApp provider interface for sending WhatsApp notifications
 */
public interface WhatsappProvider {
    
    /**
     * Send WhatsApp message using template
     * @param phoneE164 phone number in E164 format (e.g., +5491123456789)
     * @param templateName WhatsApp approved template name
     * @param variables template variables
     * @param metadata additional metadata for tracking
     * @return provider response
     * @throws NotificationProviderException if sending fails
     */
    String send(String phoneE164, String templateName, Map<String, Object> variables, Map<String, Object> metadata);
    
    /**
     * Get provider name
     */
    String getProviderName();
    
    /**
     * Check if provider is configured and ready
     */
    boolean isConfigured();
}
