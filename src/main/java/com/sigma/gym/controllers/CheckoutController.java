package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.CheckoutPreferenceDTO;
import com.sigma.gym.DTOs.CreatePreferenceRequestDTO;
import com.sigma.gym.entity.MembershipPlanEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.MembershipPlanRepository;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.security.JwtService;
import com.sigma.gym.services.MercadoPagoService;
import com.sigma.gym.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CheckoutController {
    
    private final MercadoPagoService mercadoPagoService;
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserService userService;
    private final JwtService jwtService;
    
    /**
     * Crear una preferencia de checkout para Mercado Pago
     * Endpoint específico para el frontend que devuelve exactamente el contrato esperado
     */
    @PostMapping("/create-preference")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<ResponseData<CheckoutPreferenceDTO>> createPreference(
            @Valid @RequestBody CreatePreferenceRequestDTO request,
            @RequestHeader("Authorization") String token) {
        
        try {
            // Extraer usuario del token JWT
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            UserEntity user = userService.findByEmail(email);
            
            // Buscar el plan de membresía
            Optional<MembershipPlanEntity> planOpt = membershipPlanRepository.findById(request.getPlanId());
            if (planOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ResponseData.error("Plan de membresía no encontrado"));
            }
            
            MembershipPlanEntity plan = planOpt.get();
            if (!plan.getActive()) {
                return ResponseEntity.badRequest()
                    .body(ResponseData.error("El plan de membresía no está disponible"));
            }
            
            // Crear la preferencia de checkout con Mercado Pago
            CheckoutPreferenceDTO preference = mercadoPagoService.createCheckoutPreference(user, plan);
            
            return ResponseEntity.ok(
                ResponseData.ok("Preferencia de checkout creada exitosamente", preference)
            );
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ResponseData.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Error interno al crear la preferencia de checkout: " + e.getMessage()));
        }
    }
}
