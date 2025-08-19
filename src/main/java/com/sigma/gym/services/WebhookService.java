package com.sigma.gym.services;

import com.sigma.gym.entity.PaymentLogEntity;
import com.sigma.gym.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {
    
    private final PaymentLogRepository paymentLogRepository;
    private final MembershipService membershipService;
    private final WebClient.Builder webClientBuilder;
    
    @Async
    @Transactional
    public void processPaymentWebhook(String paymentId, Map<String, Object> payload) {
        try {
            log.info("Processing payment webhook for payment ID: {}", paymentId);
            
            // Por ahora, simular el procesamiento del webhook
            // En producción, aquí consultarías la API de MercadoPago
            
            // Buscar en el payload la referencia externa
            String externalReference = extractExternalReference(payload);
            
            if (externalReference == null || externalReference.trim().isEmpty()) {
                log.error("No external reference found in webhook payload");
                return;
            }
            
            // Buscar el log de pago por referencia externa
            Optional<PaymentLogEntity> paymentLogOpt = paymentLogRepository.findByExternalReference(externalReference);
            
            if (paymentLogOpt.isEmpty()) {
                log.error("PaymentLog not found for external reference: {}", externalReference);
                return;
            }
            
            PaymentLogEntity paymentLog = paymentLogOpt.get();
            
            // Verificar si ya fue procesado
            if (paymentLog.getWebhookProcessed()) {
                log.info("Payment webhook already processed: {}", paymentId);
                return;
            }
            
            // Simular estado del pago (en producción consultarías MercadoPago)
            String paymentStatus = simulatePaymentStatus(payload);
            
            // Actualizar el log de pago
            paymentLog.setMpPaymentId(paymentId);
            paymentLog.setStatus(mapPaymentStatus(paymentStatus));
            paymentLog.setRawPayload(payload);
            paymentLog.setWebhookProcessed(true);
            
            paymentLogRepository.save(paymentLog);
            
            // Procesar según el estado del pago
            switch (paymentStatus) {
                case "approved":
                    log.info("Payment approved, activating membership: {}", externalReference);
                    membershipService.activateMembership(externalReference, paymentId);
                    break;
                    
                case "rejected":
                case "cancelled":
                    log.info("Payment {} - canceling membership: {}", paymentStatus, externalReference);
                    membershipService.cancelMembership(externalReference);
                    break;
                    
                case "pending":
                case "authorized":
                case "in_process":
                    log.info("Payment in progress: {} - {}", paymentStatus, externalReference);
                    // Mantener estado pendiente
                    break;
                    
                default:
                    log.warn("Unknown payment status: {} for payment: {}", paymentStatus, paymentId);
            }
            
            log.info("Payment webhook processed successfully: {}", paymentId);
            
        } catch (Exception e) {
            log.error("Unexpected error processing webhook for payment {}: {}", paymentId, e.getMessage(), e);
        }
    }
    
    private String extractExternalReference(Map<String, Object> payload) {
        // Intentar extraer la referencia externa del payload
        if (payload.containsKey("external_reference")) {
            return (String) payload.get("external_reference");
        }
        
        // Buscar en metadatos si existe
        if (payload.containsKey("metadata")) {
            Map<String, Object> metadata = (Map<String, Object>) payload.get("metadata");
            if (metadata != null && metadata.containsKey("external_reference")) {
                return (String) metadata.get("external_reference");
            }
        }
        
        return null;
    }
    
    private String simulatePaymentStatus(Map<String, Object> payload) {
        // En desarrollo, simular un pago aprobado
        // En producción, esto vendría de la consulta a MercadoPago API
        return "approved";
    }
    
    private PaymentLogEntity.PaymentStatus mapPaymentStatus(String mpStatus) {
        if (mpStatus == null) {
            return PaymentLogEntity.PaymentStatus.PENDING;
        }
        
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> PaymentLogEntity.PaymentStatus.APPROVED;
            case "authorized" -> PaymentLogEntity.PaymentStatus.AUTHORIZED;
            case "in_process" -> PaymentLogEntity.PaymentStatus.IN_PROCESS;
            case "rejected" -> PaymentLogEntity.PaymentStatus.REJECTED;
            case "cancelled" -> PaymentLogEntity.PaymentStatus.CANCELLED;
            case "refunded" -> PaymentLogEntity.PaymentStatus.REFUNDED;
            case "charged_back" -> PaymentLogEntity.PaymentStatus.CHARGED_BACK;
            default -> PaymentLogEntity.PaymentStatus.PENDING;
        };
    }
}
