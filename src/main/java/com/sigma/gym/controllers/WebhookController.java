package com.sigma.gym.controllers;

import com.sigma.gym.entity.PaymentLogEntity;
import com.sigma.gym.repository.PaymentLogRepository;
import com.sigma.gym.services.MembershipService;
import com.sigma.gym.services.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    
    private final WebhookService webhookService;
    private final MembershipService membershipService;
    private final PaymentLogRepository paymentLogRepository;
    
    @PostMapping("/mercadopago")
    public ResponseEntity<String> handleMercadoPagoWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String id) {
        
        try {
            log.info("Received MercadoPago webhook - Topic: {}, ID: {}", topic, id);
            log.debug("Webhook payload: {}", payload);
            
            // Validar que sea una notificación de pago
            if (!"payment".equals(topic)) {
                log.info("Ignoring webhook - not a payment notification. Topic: {}", topic);
                return ResponseEntity.ok("OK");
            }
            
            if (id == null || id.trim().isEmpty()) {
                log.error("Payment ID is missing in webhook");
                return ResponseEntity.badRequest().body("Payment ID required");
            }
            
            // Procesar el webhook de forma asíncrona
            webhookService.processPaymentWebhook(id, payload);
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error("Error processing MercadoPago webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook");
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testWebhook() {
        return ResponseEntity.ok("Webhook endpoint is working");
    }
    
    // Endpoint para debugging - solo en desarrollo
    @GetMapping("/payment-logs/{externalReference}")
    public ResponseEntity<PaymentLogEntity> getPaymentLog(@PathVariable String externalReference) {
        Optional<PaymentLogEntity> paymentLog = paymentLogRepository.findByExternalReference(externalReference);
        
        if (paymentLog.isPresent()) {
            return ResponseEntity.ok(paymentLog.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
