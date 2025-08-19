package com.sigma.gym.listeners;

import com.sigma.gym.entity.NotificationEvent;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.events.InvoiceIssuedEvent;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.notification.core.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceNotificationListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void handleInvoiceIssued(InvoiceIssuedEvent event) {
        log.info("Handling invoice issued event for invoice: {} and user: {}", 
            event.getInvoiceNumber(), event.getUserId());

        try {
            UserEntity user = userRepository.findById(event.getUserId())
                .orElse(null);
            
            if (user == null) {
                log.warn("User not found for invoice notification: {}", event.getUserId());
                return;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", user.getFirstName());
            variables.put("lastName", user.getLastName());
            variables.put("invoiceNumber", event.getInvoiceNumber());
            variables.put("issueDate", event.getEventTime().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            // Send notification with invoice attached
            notificationService.sendNotification(user, NotificationEvent.INVOICE_ISSUED, variables);
            
        } catch (Exception e) {
            log.error("Failed to send invoice notification for invoice: {}", event.getInvoiceNumber(), e);
        }
    }
}
