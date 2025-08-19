package com.sigma.gym.controllers;

import com.sigma.gym.entity.*;
import com.sigma.gym.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for notification preferences management
 */
@RestController
@RequestMapping("/api/notifications/preferences")
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferenceController {
    
    private final NotificationPreferenceRepository preferenceRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    
    /**
     * Get user's notification preferences
     */
    @GetMapping
    public ResponseEntity<NotificationPreferenceEntity> getPreferences(Authentication auth) {
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        NotificationPreferenceEntity preferences = preferenceRepository
            .findByUserId(user.getId())
            .orElse(createDefaultPreferences(user));
        
        return ResponseEntity.ok(preferences);
    }
    
    /**
     * Update user's notification preferences
     */
    @PutMapping
    public ResponseEntity<NotificationPreferenceEntity> updatePreferences(
            @RequestBody NotificationPreferenceEntity preferences,
            Authentication auth) {
        
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Ensure user can only update their own preferences
        preferences.setUser(user);
        
        NotificationPreferenceEntity saved = preferenceRepository.save(preferences);
        
        log.info("Updated notification preferences for user: {}", user.getId());
        
        return ResponseEntity.ok(saved);
    }
    
    /**
     * Register device token for push notifications
     */
    @PostMapping("/device-token")
    public ResponseEntity<String> registerDeviceToken(
            @RequestBody DeviceTokenRequest request,
            Authentication auth) {
        
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if token already exists
        Optional<DeviceTokenEntity> existing = deviceTokenRepository.findByUserAndToken(user, request.getToken());
        
        if (existing.isPresent()) {
            // Update existing token
            DeviceTokenEntity existingToken = existing.get();
            existingToken.setPlatform(request.getPlatform());
            existingToken.updateLastSeen();
            deviceTokenRepository.save(existingToken);
            
            log.info("Updated existing device token for user: {}", user.getId());
            return ResponseEntity.ok("Device token updated");
        } else {
            // Create new token
            DeviceTokenEntity deviceToken = new DeviceTokenEntity();
            deviceToken.setUser(user);
            deviceToken.setToken(request.getToken());
            deviceToken.setPlatform(request.getPlatform());
            deviceToken.setProvider(PushProvider.FCM);
            deviceToken.setIsActive(true);
            deviceToken.setCreatedAt(LocalDateTime.now());
            deviceToken.setLastSeenAt(LocalDateTime.now());
            
            deviceTokenRepository.save(deviceToken);
            
            log.info("Registered new device token for user: {}", user.getId());
            return ResponseEntity.ok("Device token registered");
        }
    }
    
    /**
     * Remove device token
     */
    @DeleteMapping("/device-token")
    public ResponseEntity<String> removeDeviceToken(
            @RequestParam String token,
            Authentication auth) {
        
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<DeviceTokenEntity> existingToken = deviceTokenRepository.findByUserAndToken(user, token);
        
        if (existingToken.isPresent()) {
            deviceTokenRepository.delete(existingToken.get());
            log.info("Removed device token for user: {}", user.getId());
            return ResponseEntity.ok("Device token removed");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user's device tokens
     */
    @GetMapping("/device-tokens")
    public ResponseEntity<List<DeviceTokenEntity>> getDeviceTokens(Authentication auth) {
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DeviceTokenEntity> tokens = deviceTokenRepository.findByUserIdAndIsActiveTrue(user.getId());
        
        return ResponseEntity.ok(tokens);
    }
    
    /**
     * Get notification history for user
     */
    @GetMapping("/history")
    public ResponseEntity<String> getNotificationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        
        // TODO: Implement notification history when NotificationLogRepository has findByUserId method
        return ResponseEntity.ok("Notification history not yet implemented");
    }
    
    /**
     * Admin endpoint: Get all notification preferences
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationPreferenceEntity>> getAllPreferences() {
        List<NotificationPreferenceEntity> preferences = preferenceRepository.findAll();
        return ResponseEntity.ok(preferences);
    }
    
    private NotificationPreferenceEntity createDefaultPreferences(UserEntity user) {
        NotificationPreferenceEntity preferences = new NotificationPreferenceEntity();
        preferences.setUser(user);
        preferences.setEmailEnabled(true);
        preferences.setPushEnabled(true);
        preferences.setWhatsappEnabled(false);
        
        return preferenceRepository.save(preferences);
    }
    
    /**
     * Device token registration request DTO
     */
    public static class DeviceTokenRequest {
        private String token;
        private DevicePlatform platform;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public DevicePlatform getPlatform() { return platform; }
        public void setPlatform(DevicePlatform platform) { this.platform = platform; }
    }
}
