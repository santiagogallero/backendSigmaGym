package com.sigma.gym.services.notification.events;

import com.sigma.gym.entity.NotificationEvent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Domain events for payment-related notifications
 */
public class PaymentEvents {
    
    @Getter
    public static class PaymentSucceeded extends ApplicationEvent {
        private final Long userId;
        private final String paymentId;
        private final BigDecimal amount;
        private final Map<String, Object> paymentDetails;
        
        public PaymentSucceeded(Object source, Long userId, String paymentId, 
                               BigDecimal amount, Map<String, Object> paymentDetails) {
            super(source);
            this.userId = userId;
            this.paymentId = paymentId;
            this.amount = amount;
            this.paymentDetails = paymentDetails;
        }
        
        public NotificationEventData toNotificationEvent() {
            Map<String, Object> variables = Map.of(
                "paymentId", paymentId,
                "amount", amount.toString(),
                "currency", paymentDetails.getOrDefault("currency", "ARS"),
                "description", paymentDetails.getOrDefault("description", "Pago de membresía"),
                "paymentDate", paymentDetails.getOrDefault("paymentDate", ""),
                "receiptUrl", paymentDetails.getOrDefault("receiptUrl", "")
            );
            
            return new NotificationEventData(userId, NotificationEvent.PAYMENT_SUCCEEDED, variables);
        }
    }
    
    @Getter
    public static class PaymentFailed extends ApplicationEvent {
        private final Long userId;
        private final String paymentId;
        private final BigDecimal amount;
        private final String errorMessage;
        private final Map<String, Object> paymentDetails;
        
        public PaymentFailed(Object source, Long userId, String paymentId, 
                           BigDecimal amount, String errorMessage, Map<String, Object> paymentDetails) {
            super(source);
            this.userId = userId;
            this.paymentId = paymentId;
            this.amount = amount;
            this.errorMessage = errorMessage;
            this.paymentDetails = paymentDetails;
        }
        
        public NotificationEventData toNotificationEvent() {
            Map<String, Object> variables = Map.of(
                "paymentId", paymentId,
                "amount", amount.toString(),
                "currency", paymentDetails.getOrDefault("currency", "ARS"),
                "description", paymentDetails.getOrDefault("description", "Pago de membresía"),
                "errorMessage", errorMessage != null ? errorMessage : "Error desconocido",
                "retryUrl", paymentDetails.getOrDefault("retryUrl", "")
            );
            
            return new NotificationEventData(userId, NotificationEvent.PAYMENT_FAILED, variables);
        }
    }
    
    @Getter
    public static class PaymentDue extends ApplicationEvent {
        private final Long userId;
        private final BigDecimal amount;
        private final Map<String, Object> membershipDetails;
        
        public PaymentDue(Object source, Long userId, BigDecimal amount, Map<String, Object> membershipDetails) {
            super(source);
            this.userId = userId;
            this.amount = amount;
            this.membershipDetails = membershipDetails;
        }
        
        public NotificationEventData toNotificationEvent() {
            Map<String, Object> variables = Map.of(
                "amount", amount.toString(),
                "currency", membershipDetails.getOrDefault("currency", "ARS"),
                "dueDate", membershipDetails.getOrDefault("dueDate", ""),
                "membershipType", membershipDetails.getOrDefault("membershipType", ""),
                "paymentUrl", membershipDetails.getOrDefault("paymentUrl", "")
            );
            
            return new NotificationEventData(userId, NotificationEvent.PAYMENT_DUE, variables);
        }
    }
}
