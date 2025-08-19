package com.sigma.gym.services.notification.events;

import com.sigma.gym.entity.NotificationEvent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Domain events for booking-related notifications
 */
public class BookingEvents {
    
    @Getter
    public static class BookingConfirmed extends ApplicationEvent {
        private final Long userId;
        private final Long bookingId;
        private final Map<String, Object> bookingDetails;
        
        public BookingConfirmed(Object source, Long userId, Long bookingId, Map<String, Object> bookingDetails) {
            super(source);
            this.userId = userId;
            this.bookingId = bookingId;
            this.bookingDetails = bookingDetails;
        }
        
        public NotificationEventData toNotificationEvent() {
            Map<String, Object> variables = Map.of(
                "bookingId", bookingId,
                "serviceName", bookingDetails.getOrDefault("serviceName", ""),
                "appointmentDate", bookingDetails.getOrDefault("appointmentDate", ""),
                "trainerName", bookingDetails.getOrDefault("trainerName", "")
            );
            
            return new NotificationEventData(userId, NotificationEvent.BOOKING_CREATED, variables);
        }
    }
    
    @Getter
    public static class BookingCancelled extends ApplicationEvent {
        private final Long userId;
        private final Long bookingId;
        private final Map<String, Object> bookingDetails;
        private final String reason;
        
        public BookingCancelled(Object source, Long userId, Long bookingId, 
                              Map<String, Object> bookingDetails, String reason) {
            super(source);
            this.userId = userId;
            this.bookingId = bookingId;
            this.bookingDetails = bookingDetails;
            this.reason = reason;
        }
        
        public NotificationEventData toNotificationEvent() {
            Map<String, Object> variables = Map.of(
                "bookingId", bookingId,
                "serviceName", bookingDetails.getOrDefault("serviceName", ""),
                "appointmentDate", bookingDetails.getOrDefault("appointmentDate", ""),
                "reason", reason != null ? reason : "No especificado"
            );
            
            return new NotificationEventData(userId, NotificationEvent.BOOKING_CANCELLED, variables);
        }
    }
    
    @Getter
    public static class BookingReminder extends ApplicationEvent {
        private final Long userId;
        private final Long bookingId;
        private final Map<String, Object> bookingDetails;
        
        public BookingReminder(Object source, Long userId, Long bookingId, Map<String, Object> bookingDetails) {
            super(source);
            this.userId = userId;
            this.bookingId = bookingId;
            this.bookingDetails = bookingDetails;
        }
        
        public NotificationEventData toNotificationEvent() {
            Map<String, Object> variables = Map.of(
                "bookingId", bookingId,
                "serviceName", bookingDetails.getOrDefault("serviceName", ""),
                "appointmentDate", bookingDetails.getOrDefault("appointmentDate", ""),
                "trainerName", bookingDetails.getOrDefault("trainerName", ""),
                "timeUntilAppointment", bookingDetails.getOrDefault("timeUntilAppointment", "1 hora")
            );
            
            return new NotificationEventData(userId, NotificationEvent.BOOKING_REMINDER, variables);
        }
    }
}
