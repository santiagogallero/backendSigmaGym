package com.sigma.gym.services;

import com.sigma.gym.DTOs.CheckoutPreferenceDTO;
import com.sigma.gym.config.MercadoPagoProperties;
import com.sigma.gym.entity.MembershipEntity;
import com.sigma.gym.entity.MembershipPlanEntity;
import com.sigma.gym.entity.PaymentLogEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.MembershipRepository;
import com.sigma.gym.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoService {
    
    private final MercadoPagoProperties mercadoPagoProperties;
    private final MembershipRepository membershipRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final WebClient.Builder webClientBuilder;
    
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
            
            // Crear request para MercadoPago API
            Map<String, Object> preferenceRequest = createPreferenceRequest(user, plan, externalReference);
            
            // Llamar a MercadoPago API
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
    
    private Map<String, Object> createPreferenceRequest(UserEntity user, MembershipPlanEntity plan, String externalReference) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", plan.getId().toString());
        item.put("title", plan.getName());
        item.put("description", plan.getDescription());
        item.put("category_id", "memberships");
        item.put("quantity", 1);
        item.put("currency_id", plan.getCurrency());
        item.put("unit_price", plan.getPrice());
        
        Map<String, Object> backUrls = new HashMap<>();
        backUrls.put("success", "http://localhost:5173/payment/success");
        backUrls.put("failure", "http://localhost:5173/payment/failure");
        backUrls.put("pending", "http://localhost:5173/payment/pending");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("user_id", user.getId().toString());
        metadata.put("plan_id", plan.getId().toString());
        metadata.put("external_reference", externalReference);
        
        Map<String, Object> preferenceRequest = new HashMap<>();
        preferenceRequest.put("items", List.of(item));
        preferenceRequest.put("back_urls", backUrls);
        preferenceRequest.put("auto_return", "approved");
        preferenceRequest.put("external_reference", externalReference);
        preferenceRequest.put("notification_url", mercadoPagoProperties.getNotificationUrl());
        preferenceRequest.put("metadata", metadata);
        preferenceRequest.put("expires", true);
        preferenceRequest.put("expiration_date_from", LocalDateTime.now().toString());
        preferenceRequest.put("expiration_date_to", LocalDateTime.now().plusHours(24).toString());
        
        return preferenceRequest;
    }
    
    private String generateExternalReference(Long userId, Long planId) {
        return String.format("MEMBERSHIP_%d_%d_%s", 
                           userId, 
                           planId, 
                           UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
