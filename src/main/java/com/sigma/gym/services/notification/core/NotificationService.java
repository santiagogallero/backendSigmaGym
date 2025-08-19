package com.sigma.gym.services.notification.core;

import com.sigma.gym.entity.*;
import com.sigma.gym.repository.*;
import com.sigma.gym.services.notification.providers.EmailProvider;
import com.sigma.gym.services.notification.providers.PushProvider;
import com.sigma.gym.services.notification.providers.WhatsappProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core notification service that handles multi-channel notification sending
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;
    private final NotificationRateLimitRepository rateLimitRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    
    private final NotificationRenderer renderer;
    private final Optional<EmailProvider> emailProvider;
    private final Optional<PushProvider> pushProvider;
    private final Optional<WhatsappProvider> whatsappProvider;
    
    /**
     * Send notification to a single user across all enabled channels
     */
    @Transactional
    public NotificationResult sendNotification(Long userId, NotificationEvent event, 
                                             Map<String, Object> variables) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        return sendNotification(user, event, variables);
    }
    
    /**
     * Send notification to a user across all enabled channels
     */
    @Transactional
    public NotificationResult sendNotification(UserEntity user, NotificationEvent event, 
                                             Map<String, Object> variables) {
        log.info("Sending {} notification to user: {}", event, user.getId());
        
        NotificationResult result = new NotificationResult();
        
        // Get user preferences
        NotificationPreferenceEntity preferences = preferenceRepository
            .findByUserId(user.getId())
            .orElse(createDefaultPreferences(user));
        
        // Send across all enabled channels
        for (NotificationChannel channel : NotificationChannel.values()) {
            if (preferences.isEventChannelEnabled(event, channel) && !isRateLimited(user, event)) {
                try {
                    String response = sendSingleChannelNotification(user, event, channel, variables);
                    result.addSuccess(channel, response);
                    
                    // Update rate limiting
                    updateRateLimit(user, event);
                } catch (Exception e) {
                    log.error("Failed to send {} notification via {} to user: {}", event, channel, user.getId(), e);
                    result.addFailure(channel, e.getMessage());
                    
                    // Log failed attempt
                    logNotificationAttempt(user, event, channel, variables, NotificationStatus.FAILED, e.getMessage());
                }
            } else {
                result.addSkipped(channel, preferences.isEventChannelEnabled(event, channel) ? 
                    "Rate limited" : "Channel disabled");
            }
        }
        
        log.info("Notification sending completed for user {} - Sent: {}, Failed: {}, Skipped: {}", 
            user.getId(), result.getSuccessCount(), result.getFailureCount(), result.getSkippedCount());
        
        return result;
    }
    
    /**
     * Send notification to multiple users
     */
    @Transactional
    public Map<Long, NotificationResult> sendBatchNotification(List<Long> userIds, NotificationEvent event, 
                                                              Map<String, Object> variables) {
        Map<Long, NotificationResult> results = new HashMap<>();
        
        List<UserEntity> users = userRepository.findAllById(userIds);
        
        for (UserEntity user : users) {
            try {
                NotificationResult result = sendNotification(user, event, variables);
                results.put(user.getId(), result);
            } catch (Exception e) {
                log.error("Failed to send notification to user: {}", user.getId(), e);
                NotificationResult errorResult = new NotificationResult();
                errorResult.addFailure(NotificationChannel.EMAIL, e.getMessage());
                results.put(user.getId(), errorResult);
            }
        }
        
        return results;
    }
    
    private String sendSingleChannelNotification(UserEntity user, NotificationEvent event, 
                                               NotificationChannel channel, Map<String, Object> variables) {
        // Get template
        NotificationTemplateEntity template = templateRepository
            .findByEventTypeAndChannelAndIsActiveTrue(event, channel)
            .orElseThrow(() -> new RuntimeException("Template not found for event: " + event + ", channel: " + channel));
        
        // Render template with variables
        NotificationRenderer.RenderedTemplate rendered = renderer.render(
            template.getSubject(), 
            template.getBody(), 
            variables
        );
        
        String response;
        
        switch (channel) {
            case EMAIL:
                if (emailProvider.isEmpty()) {
                    throw new RuntimeException("Email provider not configured");
                }
                response = emailProvider.get().send(
                    user.getEmail(),
                    rendered.getSubject(),
                    rendered.getBody(),
                    createMetadata(user, event, channel, variables)
                );
                break;
                
            case PUSH:
                if (pushProvider.isEmpty()) {
                    throw new RuntimeException("Push provider not configured");
                }
                
                // Get user's device tokens
                List<DeviceTokenEntity> tokens = deviceTokenRepository.findByUserIdAndIsActiveTrue(user.getId());
                if (tokens.isEmpty()) {
                    throw new RuntimeException("No active device tokens found for user");
                }
                
                List<String> deviceTokens = tokens.stream()
                    .map(DeviceTokenEntity::getToken)
                    .collect(Collectors.toList());
                
                response = pushProvider.get().send(
                    deviceTokens,
                    rendered.getSubject(),
                    rendered.getBody(),
                    createMetadata(user, event, channel, variables)
                );
                break;
                
            case WHATSAPP:
                // Skip WhatsApp for now since UserEntity doesn't have phone field
                throw new RuntimeException("WhatsApp notifications not supported - user phone number not available");
                
            default:
                throw new RuntimeException("Unsupported channel: " + channel);
        }
        
        // Log successful attempt
        logNotificationAttempt(user, event, channel, variables, NotificationStatus.SENT, response);
        
        return response;
    }
    
    private boolean isRateLimited(UserEntity user, NotificationEvent event) {
        Optional<NotificationRateLimitEntity> rateLimitOpt = rateLimitRepository
            .findByUserAndEventType(user, event);
        
        if (rateLimitOpt.isEmpty()) {
            return false;
        }
        
        NotificationRateLimitEntity rateLimit = rateLimitOpt.get();
        return !rateLimit.canSend();
    }
    
    private void updateRateLimit(UserEntity user, NotificationEvent event) {
        Optional<NotificationRateLimitEntity> rateLimitOpt = rateLimitRepository
            .findByUserAndEventType(user, event);
        
        NotificationRateLimitEntity rateLimit;
        if (rateLimitOpt.isPresent()) {
            rateLimit = rateLimitOpt.get();
            rateLimit.recordSent();
        } else {
            rateLimit = new NotificationRateLimitEntity();
            rateLimit.setUser(user);
            rateLimit.setEventType(event);
            rateLimit.setLastSentAt(LocalDateTime.now());
            rateLimit.setWindowStart(LocalDateTime.now());
            rateLimit.setCountInWindow(1);
        }
        
        rateLimitRepository.save(rateLimit);
    }
    
    private NotificationPreferenceEntity createDefaultPreferences(UserEntity user) {
        NotificationPreferenceEntity preferences = new NotificationPreferenceEntity();
        preferences.setUser(user);
        
        // Enable all channels by default (except WhatsApp which is opt-in)
        preferences.setEmailEnabled(true);
        preferences.setPushEnabled(true);
        preferences.setWhatsappEnabled(false);
        
        return preferenceRepository.save(preferences);
    }
    
    private void logNotificationAttempt(UserEntity user, NotificationEvent event, NotificationChannel channel,
                                      Map<String, Object> variables, NotificationStatus status, String response) {
        try {
            NotificationLogEntity log = new NotificationLogEntity();
            log.setUser(user);
            log.setEventType(event);
            log.setChannel(channel);
            log.setStatus(status);
            log.setProviderResponse(response);
            log.setPayloadJson(variables.toString()); // Convert to JSON string
            log.setCreatedAt(LocalDateTime.now());
            
            logRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to log notification attempt", e);
        }
    }
    
    private Map<String, Object> createMetadata(UserEntity user, NotificationEvent event, 
                                             NotificationChannel channel, Map<String, Object> variables) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("userId", user.getId());
        metadata.put("event", event.toString());
        metadata.put("channel", channel.toString());
        metadata.put("timestamp", LocalDateTime.now().toString());
        metadata.put("variables", variables);
        return metadata;
    }
    
    /**
     * Result of notification sending operation
     */
    public static class NotificationResult {
        private final Map<NotificationChannel, String> successes = new HashMap<>();
        private final Map<NotificationChannel, String> failures = new HashMap<>();
        private final Map<NotificationChannel, String> skipped = new HashMap<>();
        
        public void addSuccess(NotificationChannel channel, String response) {
            successes.put(channel, response);
        }
        
        public void addFailure(NotificationChannel channel, String error) {
            failures.put(channel, error);
        }
        
        public void addSkipped(NotificationChannel channel, String reason) {
            skipped.put(channel, reason);
        }
        
        public int getSuccessCount() {
            return successes.size();
        }
        
        public int getFailureCount() {
            return failures.size();
        }
        
        public int getSkippedCount() {
            return skipped.size();
        }
        
        public Map<NotificationChannel, String> getSuccesses() {
            return successes;
        }
        
        public Map<NotificationChannel, String> getFailures() {
            return failures;
        }
        
        public Map<NotificationChannel, String> getSkipped() {
            return skipped;
        }
        
        public boolean hasFailures() {
            return !failures.isEmpty();
        }
        
        public boolean isComplete() {
            return !successes.isEmpty() || !failures.isEmpty();
        }
    }
}
