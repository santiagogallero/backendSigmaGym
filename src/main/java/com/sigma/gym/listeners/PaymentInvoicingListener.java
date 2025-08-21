package com.sigma.gym.listeners;

import com.sigma.gym.repository.InvoiceRepository;
import com.sigma.gym.services.InvoiceService;
import com.sigma.gym.services.notification.events.PaymentEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentInvoicingListener {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    
    // Cache to prevent duplicate processing of the same payment event
    private final ConcurrentMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();
    
    // Guard to prevent overlapping scheduled executions
    private final AtomicBoolean scheduledRunning = new AtomicBoolean(false);

    /**
     * Listen for payment succeeded events and automatically issue invoices
     * Only listens to PaymentEvents.PaymentSucceeded to avoid capturing all Spring events
     */
    @Async
    @EventListener
    public void handlePaymentSucceeded(PaymentEvents.PaymentSucceeded event) {
        String paymentId = event.getPaymentId();
        
        // Generate unique event ID to prevent duplicate processing
        String eventId = "PAYMENT_" + paymentId + "_" + event.getTimestamp();
        
        // Check if this event has already been processed
        if (processedEvents.putIfAbsent(eventId, true) != null) {
            log.debug("Payment event {} already processed, skipping", eventId);
            return;
        }
        
        log.debug("Processing payment succeeded event for payment ID: {}", paymentId);

        try {
            // Check idempotency at repository level first
            Long paymentIdLong = Long.parseLong(paymentId);
            if (invoiceRepository.existsByPaymentId(paymentIdLong)) {
                log.debug("Invoice already exists for payment {}, skipping", paymentId);
                return;
            }
            
            // Extract payment details
            String description = (String) event.getPaymentDetails()
                .getOrDefault("description", "Pago de membresía");
            String currency = (String) event.getPaymentDetails()
                .getOrDefault("currency", "ARS");
            
            // Generate invoice using the idempotent method
            invoiceService.generateIfNotExists(
                paymentIdLong,
                description,
                event.getAmount(),
                currency
            );
            
            log.info("Invoice generated successfully for payment: {}", paymentId);
            
        } catch (NumberFormatException e) {
            log.warn("Invalid payment ID format: {}, skipping invoice generation", paymentId);
            processedEvents.remove(eventId); // Allow retry with correct format
        } catch (Exception e) {
            log.error("Failed to generate invoice for payment {}: {}", paymentId, e.getMessage());
            // Remove from processed cache to allow retry
            processedEvents.remove(eventId);
            // In production, you might want to:
            // 1. Store failed attempts for retry
            // 2. Send alerts to administrators
            // 3. Queue for manual processing
        }
    }
    
    /**
     * Scheduled sweep for pending payments that need invoicing
     * Runs every 60 seconds but prevents overlapping executions
     */
    @Scheduled(fixedDelay = 60000) // 60 seconds
    public void processePendingInvoices() {
        // Prevent overlapping executions
        if (!scheduledRunning.compareAndSet(false, true)) {
            log.debug("Previous scheduled invoicing still running, skipping this cycle");
            return;
        }
        
        try {
            log.debug("Starting scheduled pending invoices processing");
            int created = 0;
            int skipped = 0;
            
            // TODO: Implement actual logic to find payments that need invoicing
            // This should query payments that:
            // 1. Are successfully completed
            // 2. Don't have corresponding invoices yet
            // 3. Are eligible for auto-invoicing
            
            // Example implementation:
            // List<Payment> pendingPayments = paymentRepository.findSuccessfulWithoutInvoices();
            // 
            // for (Payment payment : pendingPayments) {
            //     if (invoiceRepository.existsByPaymentId(payment.getId())) {
            //         skipped++;
            //         continue;
            //     }
            //     
            //     try {
            //         invoiceService.generateIfNotExists(
            //             payment.getId(),
            //             payment.getDescription(),
            //             payment.getAmount(),
            //             payment.getCurrency()
            //         );
            //         created++;
            //     } catch (Exception e) {
            //         log.warn("Failed to generate invoice for payment {}: {}", payment.getId(), e.getMessage());
            //         skipped++;
            //     }
            // }
            
            // Only log summary if there was activity
            if (created > 0 || skipped > 0) {
                log.info("Auto-invoicing completed: {} invoices created, {} skipped", created, skipped);
            }
            
        } catch (Exception e) {
            log.error("Error during scheduled invoice processing", e);
        } finally {
            scheduledRunning.set(false);
        }
    }
    
    /**
     * Manual trigger method for testing invoice generation
     * This can be removed in production or kept for administrative purposes
     */
    public void triggerTestInvoice(Long paymentId, String description, BigDecimal amount, String currency) {
        String eventId = "MANUAL_TEST_" + paymentId + "_" + System.currentTimeMillis();
        
        // Check idempotency first at repository level
        if (invoiceRepository.existsByPaymentId(paymentId)) {
            log.debug("Invoice already exists for payment {}, skipping test trigger", paymentId);
            return;
        }
        
        // Prevent duplicate manual triggers in cache
        if (processedEvents.putIfAbsent(eventId, true) != null) {
            log.debug("Test invoice for payment {} already triggered recently", paymentId);
            return;
        }
        
        log.info("Triggering test invoice for payment: {} with event ID: {}", paymentId, eventId);
        
        try {
            invoiceService.issueForPayment(paymentId, description, amount, currency);
            log.info("Test invoice generated successfully for payment: {}", paymentId);
        } catch (Exception e) {
            log.error("Failed to generate test invoice for payment: {}", paymentId, e);
            // Remove from cache to allow retry
            processedEvents.remove(eventId);
            throw e;
        }
    }
    
    /**
     * Clear processed events cache (for administrative purposes)
     * Should be called periodically or when system restarts
     */
    public void clearProcessedEventsCache() {
        int cacheSize = processedEvents.size();
        processedEvents.clear();
        log.info("Cleared processed events cache, removed {} entries", cacheSize);
    }
}
