package com.sigma.gym.controllers;

import com.sigma.gym.entity.*;
import com.sigma.gym.services.notification.core.NotificationService;
import com.sigma.gym.services.notification.core.NotificationService.NotificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for notification management
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * Send notification to a single user
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public ResponseEntity<NotificationResult> sendNotification(
            @RequestParam Long userId,
            @RequestParam NotificationEvent event,
            @RequestBody Map<String, Object> variables) {
        
        log.info("Sending notification to user {} for event {}", userId, event);
        
        NotificationResult result = notificationService.sendNotification(userId, event, variables);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Send notification to multiple users
     */
    @PostMapping("/send-batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public ResponseEntity<Map<Long, NotificationResult>> sendBatchNotification(
            @RequestBody BatchNotificationRequest request) {
        
        log.info("Sending batch notification to {} users for event {}", 
            request.getUserIds().size(), request.getEvent());
        
        Map<Long, NotificationResult> results = notificationService.sendBatchNotification(
            request.getUserIds(), 
            request.getEvent(), 
            request.getVariables()
        );
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Test notification sending (for development/testing)
     */
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResult> testNotification(
            @RequestParam Long userId,
            @RequestParam NotificationChannel channel) {
        
        log.info("Testing notification for user {} via channel {}", userId, channel);
        
        // Create test variables
        Map<String, Object> testVariables = Map.of(
            "userName", "Usuario de Prueba",
            "gymName", "Sigma Gym",
            "testMessage", "Esta es una notificación de prueba"
        );
        
        // Use a booking event for testing
        NotificationResult result = notificationService.sendNotification(
            userId, 
            NotificationEvent.BOOKING_CREATED, 
            testVariables
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Request DTO for batch notifications
     */
    public static class BatchNotificationRequest {
        private List<Long> userIds;
        private NotificationEvent event;
        private Map<String, Object> variables;
        
        // Getters and setters
        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
        
        public NotificationEvent getEvent() { return event; }
        public void setEvent(NotificationEvent event) { this.event = event; }
        
        public Map<String, Object> getVariables() { return variables; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
    }
}
