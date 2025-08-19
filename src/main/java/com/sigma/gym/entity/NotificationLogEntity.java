package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit log for all notification attempts
 */
@Entity
@Table(name = "notification_logs",
       indexes = {
           @Index(name = "idx_notification_logs_user_event", columnList = "user_id, event_type"),
           @Index(name = "idx_notification_logs_status_created", columnList = "status, created_at"),
           @Index(name = "idx_notification_logs_created_at", columnList = "created_at")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private NotificationEvent eventType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;
    
    @Column(name = "recipient", nullable = false, length = 500)
    private String recipient; // email, phone number, or device token
    
    @Column(name = "subject", length = 200)
    private String subject;
    
    @Column(name = "payload_json", columnDefinition = "JSON")
    private String payloadJson; // Full payload sent to the provider
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;
    
    @Column(name = "provider_response", columnDefinition = "TEXT")
    private String providerResponse;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 3;
    
    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Mark as sent successfully
     */
    public void markAsSent(String providerResponse) {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.providerResponse = providerResponse;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }
    
    /**
     * Mark as failed and schedule retry if retries available
     */
    public void markAsFailed(String errorMessage, String providerResponse) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.providerResponse = providerResponse;
        
        if (this.retryCount < this.maxRetries) {
            this.status = NotificationStatus.RETRY_PENDING;
            this.retryCount++;
            // Exponential backoff: 2^retryCount minutes
            int delayMinutes = (int) Math.pow(2, this.retryCount);
            this.nextRetryAt = LocalDateTime.now().plusMinutes(delayMinutes);
        }
    }
    
    /**
     * Check if this notification is eligible for retry
     */
    public boolean canRetry() {
        return this.status == NotificationStatus.RETRY_PENDING 
            && this.nextRetryAt != null 
            && this.nextRetryAt.isBefore(LocalDateTime.now());
    }
}
