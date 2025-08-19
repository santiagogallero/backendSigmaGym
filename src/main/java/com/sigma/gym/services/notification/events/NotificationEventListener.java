package com.sigma.gym.services.notification.events;

import com.sigma.gym.services.notification.core.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener that handles domain events and triggers notifications
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    
    private final NotificationService notificationService;
    
    /**
     * Handle booking events after transaction commits
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingConfirmed(BookingEvents.BookingConfirmed event) {
        log.info("Processing booking confirmed event for user: {}, booking: {}", 
            event.getUserId(), event.getBookingId());
        
        try {
            NotificationEventData notificationEvent = event.toNotificationEvent();
            notificationService.sendNotification(
                notificationEvent.getUserId(), 
                notificationEvent.getEventType(), 
                notificationEvent.getVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send booking confirmed notification for user: {}, booking: {}", 
                event.getUserId(), event.getBookingId(), e);
        }
    }
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingCancelled(BookingEvents.BookingCancelled event) {
        log.info("Processing booking cancelled event for user: {}, booking: {}", 
            event.getUserId(), event.getBookingId());
        
        try {
            NotificationEventData notificationEvent = event.toNotificationEvent();
            notificationService.sendNotification(
                notificationEvent.getUserId(), 
                notificationEvent.getEventType(), 
                notificationEvent.getVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send booking cancelled notification for user: {}, booking: {}", 
                event.getUserId(), event.getBookingId(), e);
        }
    }
    
    @Async
    @EventListener
    public void handleBookingReminder(BookingEvents.BookingReminder event) {
        log.info("Processing booking reminder event for user: {}, booking: {}", 
            event.getUserId(), event.getBookingId());
        
        try {
            NotificationEventData notificationEvent = event.toNotificationEvent();
            notificationService.sendNotification(
                notificationEvent.getUserId(), 
                notificationEvent.getEventType(), 
                notificationEvent.getVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send booking reminder notification for user: {}, booking: {}", 
                event.getUserId(), event.getBookingId(), e);
        }
    }
    
    /**
     * Handle payment events after transaction commits
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSucceeded(PaymentEvents.PaymentSucceeded event) {
        log.info("Processing payment succeeded event for user: {}, payment: {}", 
            event.getUserId(), event.getPaymentId());
        
        try {
            NotificationEventData notificationEvent = event.toNotificationEvent();
            notificationService.sendNotification(
                notificationEvent.getUserId(), 
                notificationEvent.getEventType(), 
                notificationEvent.getVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send payment succeeded notification for user: {}, payment: {}", 
                event.getUserId(), event.getPaymentId(), e);
        }
    }
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentFailed(PaymentEvents.PaymentFailed event) {
        log.info("Processing payment failed event for user: {}, payment: {}", 
            event.getUserId(), event.getPaymentId());
        
        try {
            NotificationEventData notificationEvent = event.toNotificationEvent();
            notificationService.sendNotification(
                notificationEvent.getUserId(), 
                notificationEvent.getEventType(), 
                notificationEvent.getVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send payment failed notification for user: {}, payment: {}", 
                event.getUserId(), event.getPaymentId(), e);
        }
    }
    
    @Async
    @EventListener
    public void handlePaymentDue(PaymentEvents.PaymentDue event) {
        log.info("Processing payment due event for user: {}", event.getUserId());
        
        try {
            NotificationEventData notificationEvent = event.toNotificationEvent();
            notificationService.sendNotification(
                notificationEvent.getUserId(), 
                notificationEvent.getEventType(), 
                notificationEvent.getVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send payment due notification for user: {}", 
                event.getUserId(), e);
        }
    }
}
