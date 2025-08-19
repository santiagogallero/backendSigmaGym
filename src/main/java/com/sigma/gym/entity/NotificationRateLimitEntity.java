package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Rate limiting for notifications to prevent spam
 */
@Entity
@Table(name = "notification_rate_limits",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_type"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRateLimitEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private NotificationEvent eventType;
    
    @Column(name = "last_sent_at", nullable = false)
    private LocalDateTime lastSentAt;
    
    @Column(name = "count_in_window", nullable = false)
    @Builder.Default
    private Integer countInWindow = 1;
    
    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.windowStart == null) {
            this.windowStart = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if rate limit allows sending (1 notification per hour per event type)
     */
    public boolean canSend() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return this.lastSentAt.isBefore(oneHourAgo);
    }
    
    /**
     * Update the rate limit after sending
     */
    public void recordSent() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        if (this.windowStart.isBefore(oneHourAgo)) {
            // Reset the window
            this.windowStart = now;
            this.countInWindow = 1;
        } else {
            // Increment count in current window
            this.countInWindow++;
        }
        
        this.lastSentAt = now;
    }
}
