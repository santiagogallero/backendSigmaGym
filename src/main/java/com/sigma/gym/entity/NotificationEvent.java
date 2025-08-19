package com.sigma.gym.entity;

/**
 * Notification events that can trigger notifications
 */
public enum NotificationEvent {
    // Booking events
    BOOKING_CREATED,
    BOOKING_REMINDER,
    BOOKING_CANCELLED,
    
    // Waitlist events
    WAITLIST_PROMOTED,
    WAITLIST_CONFIRMED,
    WAITLIST_HOLD_EXPIRED,
    
    // Invoice events
    INVOICE_ISSUED,
    
    // Payment events
    PAYMENT_DUE,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED
}
