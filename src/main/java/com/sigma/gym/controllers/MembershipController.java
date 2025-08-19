package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.entity.MembershipPlanEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.ValidationException;
import com.sigma.gym.repository.MembershipPlanRepository;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.security.JwtService;
import com.sigma.gym.services.FreezeService;
import com.sigma.gym.services.MembershipService;
import com.sigma.gym.services.MercadoPagoService;
import com.sigma.gym.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MembershipController {
    
    private final MembershipService membershipService;
    private final MercadoPagoService mercadoPagoService;
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final FreezeService freezeService;
    
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<MembershipStatusContractDTO> getMembershipStatus(
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            UserEntity user = userService.findByEmail(email);
            
            MembershipStatusContractDTO status = membershipService.getUserMembershipStatusContract(user);
            
            return ResponseEntity.ok(status);
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MembershipStatusContractDTO.builder()
                    .status("ERROR")
                    .build());
        }
    }
    
    @GetMapping("/freeze/rules")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<FreezeRulesDTO> getFreezeRules(
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            
            FreezeRulesDTO rules = freezeService.getFreezeRules(email);
            
            return ResponseEntity.ok(rules);
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    @PostMapping("/freeze")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<FreezeResponseDTO> freezeMembership(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody FreezeRequestDTO request) {
        
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            
            FreezeResponseDTO response = freezeService.freezeMembership(email, request);
            
            return ResponseEntity.ok(response);
                
        } catch (ValidationException e) {
            if (e.getMessage().contains("409") || e.getMessage().contains("solapamiento") || 
                e.getMessage().contains("ya existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/unfreeze")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<UnfreezeResponseDTO> unfreezeMembership(
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            
            UnfreezeResponseDTO response = freezeService.unfreezeMembership(email);
            
            return ResponseEntity.ok(response);
                
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/plans")
    public ResponseEntity<ResponseData<List<MembershipPlanDTO>>> getAvailablePlans() {
        try {
            List<MembershipPlanEntity> plans = membershipPlanRepository.findActiveOrderByPriceAsc();
            
            List<MembershipPlanDTO> planDTOs = plans.stream()
                .map(this::convertToDTO)
                .toList();
            
            return ResponseEntity.ok(ResponseData.success("Planes de membresía obtenidos exitosamente", planDTOs));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Error al obtener los planes de membresía: " + e.getMessage()));
        }
    }
    
    @PostMapping("/checkout/{planId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<ResponseData<CheckoutPreferenceDTO>> createCheckout(
            @PathVariable Long planId,
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            UserEntity user = userService.findByEmail(email);
            
            Optional<MembershipPlanEntity> planOpt = membershipPlanRepository.findById(planId);
            if (planOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ResponseData.error("Plan de membresía no encontrado"));
            }
            
            MembershipPlanEntity plan = planOpt.get();
            if (!plan.getActive()) {
                return ResponseEntity.badRequest()
                    .body(ResponseData.error("El plan de membresía no está disponible"));
            }
            
            CheckoutPreferenceDTO preference = mercadoPagoService.createCheckoutPreference(user, plan);
            
            return ResponseEntity.ok(ResponseData.success("Preferencia de checkout creada exitosamente", preference));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Error al crear la preferencia de checkout: " + e.getMessage()));
        }
    }
    
    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'OWNER')")
    public ResponseEntity<ResponseData<String>> cancelMembership(
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(jwt);
            UserEntity user = userService.findByEmail(email);
            
            membershipService.cancelUserMembership(user);
            
            return ResponseEntity.ok(ResponseData.success("Membresía cancelada exitosamente", null));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Error al cancelar la membresía: " + e.getMessage()));
        }
    }
    
    // Admin endpoints
    @PostMapping("/plans")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<MembershipPlanDTO>> createPlan(
            @Valid @RequestBody MembershipPlanDTO planDTO) {
        
        try {
            MembershipPlanEntity plan = convertToEntity(planDTO);
            plan.setActive(true);
            MembershipPlanEntity savedPlan = membershipPlanRepository.save(plan);
            
            return ResponseEntity.ok(ResponseData.success("Plan de membresía creado exitosamente", convertToDTO(savedPlan)));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Error al crear el plan de membresía: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/active-count")
    @PreAuthorize("hasAnyRole('TRAINER', 'OWNER')")
    public ResponseEntity<ResponseData<Long>> getActiveMembershipsCount() {
        try {
            long count = membershipService.getActiveMembershipsCount();
            
            return ResponseEntity.ok(ResponseData.success("Cantidad de membresías activas obtenida exitosamente", count));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Error al obtener la cantidad de membresías activas: " + e.getMessage()));
        }
    }
    
    private MembershipPlanDTO convertToDTO(MembershipPlanEntity entity) {
        return MembershipPlanDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .price(entity.getPrice())
            .currency(entity.getCurrency())
            .durationDays(entity.getDurationDays())
            .active(entity.getActive())
            .build();
    }
    
    private MembershipPlanEntity convertToEntity(MembershipPlanDTO dto) {
        return MembershipPlanEntity.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .price(dto.getPrice())
            .currency(dto.getCurrency())
            .durationDays(dto.getDurationDays())
            .build();
    }
}
