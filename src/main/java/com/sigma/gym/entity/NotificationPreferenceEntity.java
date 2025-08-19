package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User notification preferences per channel and event type
 */
@Entity
@Table(name = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    // Global channel toggles
    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;
    
    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private Boolean pushEnabled = true;
    
    @Column(name = "whatsapp_enabled", nullable = false)
    @Builder.Default
    private Boolean whatsappEnabled = false;
    
    // Booking event preferences
    @Column(name = "booking_created_email", nullable = false)
    @Builder.Default
    private Boolean bookingCreatedEmail = true;
    
    @Column(name = "booking_created_push", nullable = false)
    @Builder.Default
    private Boolean bookingCreatedPush = true;
    
    @Column(name = "booking_created_whatsapp", nullable = false)
    @Builder.Default
    private Boolean bookingCreatedWhatsapp = false;
    
    @Column(name = "booking_reminder_email", nullable = false)
    @Builder.Default
    private Boolean bookingReminderEmail = true;
    
    @Column(name = "booking_reminder_push", nullable = false)
    @Builder.Default
    private Boolean bookingReminderPush = true;
    
    @Column(name = "booking_reminder_whatsapp", nullable = false)
    @Builder.Default
    private Boolean bookingReminderWhatsapp = false;
    
    @Column(name = "booking_cancelled_email", nullable = false)
    @Builder.Default
    private Boolean bookingCancelledEmail = true;
    
    @Column(name = "booking_cancelled_push", nullable = false)
    @Builder.Default
    private Boolean bookingCancelledPush = true;
    
    @Column(name = "booking_cancelled_whatsapp", nullable = false)
    @Builder.Default
    private Boolean bookingCancelledWhatsapp = false;
    
    // Payment event preferences
    @Column(name = "payment_due_email", nullable = false)
    @Builder.Default
    private Boolean paymentDueEmail = true;
    
    @Column(name = "payment_due_push", nullable = false)
    @Builder.Default
    private Boolean paymentDuePush = true;
    
    @Column(name = "payment_due_whatsapp", nullable = false)
    @Builder.Default
    private Boolean paymentDueWhatsapp = false;
    
    @Column(name = "payment_succeeded_email", nullable = false)
    @Builder.Default
    private Boolean paymentSucceededEmail = true;
    
    @Column(name = "payment_succeeded_push", nullable = false)
    @Builder.Default
    private Boolean paymentSucceededPush = true;
    
    @Column(name = "payment_succeeded_whatsapp", nullable = false)
    @Builder.Default
    private Boolean paymentSucceededWhatsapp = false;
    
    @Column(name = "payment_failed_email", nullable = false)
    @Builder.Default
    private Boolean paymentFailedEmail = true;
    
    @Column(name = "payment_failed_push", nullable = false)
    @Builder.Default
    private Boolean paymentFailedPush = true;
    
    @Column(name = "payment_failed_whatsapp", nullable = false)
    @Builder.Default
    private Boolean paymentFailedWhatsapp = false;
    
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
     * Check if a specific event and channel combination is enabled
     */
    public boolean isEventChannelEnabled(NotificationEvent event, NotificationChannel channel) {
        // First check if the global channel is enabled
        boolean channelEnabled = switch (channel) {
            case EMAIL -> emailEnabled;
            case PUSH -> pushEnabled;
            case WHATSAPP -> whatsappEnabled;
        };
        
        if (!channelEnabled) {
            return false;
        }
        
        // Then check the specific event-channel combination
        return switch (event) {
            case BOOKING_CREATED -> switch (channel) {
                case EMAIL -> bookingCreatedEmail;
                case PUSH -> bookingCreatedPush;
                case WHATSAPP -> bookingCreatedWhatsapp;
            };
            case BOOKING_REMINDER -> switch (channel) {
                case EMAIL -> bookingReminderEmail;
                case PUSH -> bookingReminderPush;
                case WHATSAPP -> bookingReminderWhatsapp;
            };
            case BOOKING_CANCELLED -> switch (channel) {
                case EMAIL -> bookingCancelledEmail;
                case PUSH -> bookingCancelledPush;
                case WHATSAPP -> bookingCancelledWhatsapp;
            };
            case PAYMENT_DUE -> switch (channel) {
                case EMAIL -> paymentDueEmail;
                case PUSH -> paymentDuePush;
                case WHATSAPP -> paymentDueWhatsapp;
            };
            case PAYMENT_SUCCEEDED -> switch (channel) {
                case EMAIL -> paymentSucceededEmail;
                case PUSH -> paymentSucceededPush;
                case WHATSAPP -> paymentSucceededWhatsapp;
            };
            case PAYMENT_FAILED -> switch (channel) {
                case EMAIL -> paymentFailedEmail;
                case PUSH -> paymentFailedPush;
                case WHATSAPP -> paymentFailedWhatsapp;
            };
            case INVOICE_ISSUED -> switch (channel) {
                case EMAIL -> true; // Invoices should always be sent via email
                case PUSH -> false; // Don't send invoices via push
                case WHATSAPP -> false; // Don't send invoices via whatsapp
            };
            default -> false; // Default to false for unknown events
        };
    }
}
