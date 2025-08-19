package com.sigma.gym.services.notification.providers;

/**
 * Exception thrown by notification providers
 */
public class NotificationProviderException extends RuntimeException {
    
    private final String providerName;
    private final String errorCode;
    
    public NotificationProviderException(String providerName, String message) {
        super(message);
        this.providerName = providerName;
        this.errorCode = null;
    }
    
    public NotificationProviderException(String providerName, String message, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.errorCode = null;
    }
    
    public NotificationProviderException(String providerName, String errorCode, String message) {
        super(message);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    public NotificationProviderException(String providerName, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
