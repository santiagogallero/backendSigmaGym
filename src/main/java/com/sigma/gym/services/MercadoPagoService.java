package com.sigma.gym.services;

import com.sigma.gym.DTOs.CheckoutPreferenceDTO;
// import com.sigma.gym.config.MercadoPagoProperties; // TODO: Re-enable when needed
import com.sigma.gym.entity.MembershipEntity;
import com.sigma.gym.entity.MembershipPlanEntity;
import com.sigma.gym.entity.PaymentLogEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.MembershipRepository;
import com.sigma.gym.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.reactive.function.client.WebClient; // TODO: Re-enable when dependency resolved

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoService {
    
    // TODO: Re-enable when MercadoPago integration is needed
    // private final MercadoPagoProperties mercadoPagoProperties;
    private final MembershipRepository membershipRepository;
    private final PaymentLogRepository paymentLogRepository;
    // TODO: Re-enable when WebClient dependency is resolved
    // private final WebClient.Builder webClientBuilder;
    
    @Transactional
    public CheckoutPreferenceDTO createCheckoutPreference(UserEntity user, MembershipPlanEntity plan) {
        try {
            // Generar referencia única
            String externalReference = generateExternalReference(user.getId(), plan.getId());
            
            // Verificar si ya existe una membresía pendiente
            membershipRepository.findByUser(user).ifPresent(membership -> {
                if (membership.getStatus() == MembershipEntity.MembershipStatus.PENDING) {
                    log.info("Canceling existing pending membership for user: {}", user.getId());
                    membership.setStatus(MembershipEntity.MembershipStatus.CANCELED);
                    membershipRepository.save(membership);
                }
            });
            
            // Crear nueva membresía en estado PENDING
            MembershipEntity membership = MembershipEntity.builder()
                .user(user)
                .plan(plan)
                .status(MembershipEntity.MembershipStatus.PENDING)
                .externalReference(externalReference)
                .build();
            membershipRepository.save(membership);
            
            // Crear log de pago
            PaymentLogEntity paymentLog = PaymentLogEntity.builder()
                .user(user)
                .externalReference(externalReference)
                .status(PaymentLogEntity.PaymentStatus.PENDING)
                .webhookProcessed(false)
                .build();
            paymentLogRepository.save(paymentLog);
            
            // TODO: Re-enable when WebClient dependency is resolved
            // Llamar a MercadoPago API (temporarily disabled)
            /*
            Map<String, Object> preferenceRequest = createPreferenceRequest(user, plan, externalReference);
            
            WebClient webClient = webClientBuilder
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + mercadoPagoProperties.getAccessToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
            
            Map<String, Object> response = webClient
                .post()
                .uri("/checkout/preferences")
                .bodyValue(preferenceRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            */
            
            // Temporary fallback response for development
            Map<String, Object> response = createMockResponse(externalReference);
            
            if (response == null) {
                throw new RuntimeException("No response from MercadoPago API");
            }
            
            log.info("Checkout preference created successfully. ID: {}, External Reference: {}", 
                     response.get("id"), externalReference);
            
            return CheckoutPreferenceDTO.builder()
                .preferenceId((String) response.get("id"))
                .initPoint((String) response.get("init_point"))
                .sandboxInitPoint((String) response.get("sandbox_init_point"))
                .externalReference(externalReference)
                .build();
                
        } catch (Exception e) {
            log.error("Error creating checkout preference: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear la preferencia de pago: " + e.getMessage());
        }
    }
    
    // TODO: Re-enable when WebClient dependency is resolved
    /*
    private Map<String, Object> createPreferenceRequest(UserEntity user, MembershipPlanEntity plan, String externalReference) {
        Map<String, Object> request = new HashMap<>();
        
        // Configuración del ítem
        Map<String, Object> item = new HashMap<>();
        item.put("id", plan.getId());
        item.put("title", plan.getName());
        item.put("description", "Plan de membresía " + plan.getName());
        item.put("quantity", 1);
        item.put("unit_price", plan.getPrice());
        item.put("currency_id", "ARS");
        
        request.put("items", List.of(item));
        request.put("external_reference", externalReference);
        
        // URLs de retorno
        Map<String, Object> backUrls = new HashMap<>();
        backUrls.put("success", mercadoPagoProperties.getSuccessUrl());
        backUrls.put("failure", mercadoPagoProperties.getFailureUrl());
        backUrls.put("pending", mercadoPagoProperties.getPendingUrl());
        request.put("back_urls", backUrls);
        
        // Auto return
        request.put("auto_return", "approved");
        
        // Información del pagador
        Map<String, Object> payer = new HashMap<>();
        payer.put("name", user.getFirstName());
        payer.put("email", user.getEmail());
        request.put("payer", payer);
        
        // Métodos de pago excluidos (opcional)
        Map<String, Object> paymentMethods = new HashMap<>();
        paymentMethods.put("excluded_payment_types", List.of());
        paymentMethods.put("excluded_payment_methods", List.of());
        request.put("payment_methods", paymentMethods);
        
        return request;
    }
    */
    
    private Map<String, Object> createMockResponse(String externalReference) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "MOCK_PREFERENCE_" + System.currentTimeMillis());
        response.put("init_point", "https://sandbox.mercadopago.com.ar/checkout/v1/redirect?pref_id=mock_preference");
        response.put("sandbox_init_point", "https://sandbox.mercadopago.com.ar/checkout/v1/redirect?pref_id=mock_preference");
        response.put("external_reference", externalReference);
        response.put("date_created", Instant.now().toString());
        return response;
    }
    
    private String generateExternalReference(Long userId, Long planId) {
        return String.format("MEMBERSHIP_%d_%d_%s", 
                           userId, 
                           planId, 
                           UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
