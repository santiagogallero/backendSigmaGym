package com.sigma.gym.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for notification providers
 */
@Component
@ConfigurationProperties(prefix = "notifications")
public class NotificationProperties {
    
    private Email email = new Email();
    private Push push = new Push();
    private Whatsapp whatsapp = new Whatsapp();
    
    // Getters and setters
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    
    public Push getPush() { return push; }
    public void setPush(Push push) { this.push = push; }
    
    public Whatsapp getWhatsapp() { return whatsapp; }
    public void setWhatsapp(Whatsapp whatsapp) { this.whatsapp = whatsapp; }
    
    public static class Email {
        private String provider = "SENDGRID";
        private String from = "noreply@sigmagym.com";
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
    }
    
    public static class Push {
        private String provider = "FCM";
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
    }
    
    public static class Whatsapp {
        private String provider = "META";
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
    }
}
