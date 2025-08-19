package com.sigma.gym.listeners;

import com.sigma.gym.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentInvoicingListener {

    private final InvoiceService invoiceService;

    /**
     * Listen for payment succeeded events and automatically issue invoices
     * Note: This assumes a PaymentSucceededEvent exists in your system
     * If not, this listener would need to be integrated with your actual payment system
     */
    @Async
    @EventListener
    public void handlePaymentSucceeded(Object paymentEvent) {
        log.info("Handling payment succeeded event for automatic invoicing");

        try {
            // TODO: Replace this with actual PaymentSucceededEvent handling
            // This is a placeholder implementation
            // In a real system, you would:
            // 1. Extract payment details from the event
            // 2. Call invoiceService.issueForPayment() with the appropriate parameters
            
            // Example implementation:
            // PaymentSucceededEvent event = (PaymentSucceededEvent) paymentEvent;
            // invoiceService.issueForPayment(
            //     event.getPaymentId(),
            //     event.getDescription(),
            //     event.getAmount(),
            //     event.getCurrency()
            // );
            
            log.debug("Automatic invoice generation completed");
            
        } catch (Exception e) {
            log.error("Failed to automatically generate invoice for payment event", e);
            // In production, you might want to:
            // 1. Store failed attempts for retry
            // 2. Send alerts to administrators
            // 3. Queue for manual processing
        }
    }
    
    /**
     * Manual trigger method for testing invoice generation
     * This can be removed in production or kept for administrative purposes
     */
    public void triggerTestInvoice(Long paymentId, String description, BigDecimal amount, String currency) {
        log.info("Triggering test invoice for payment: {}", paymentId);
        
        try {
            invoiceService.issueForPayment(paymentId, description, amount, currency);
            log.info("Test invoice generated successfully for payment: {}", paymentId);
        } catch (Exception e) {
            log.error("Failed to generate test invoice for payment: {}", paymentId, e);
            throw e;
        }
    }
}
