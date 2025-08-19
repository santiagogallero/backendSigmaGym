package com.sigma.gym.services;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.entity.FreezeRulesEntity;
import com.sigma.gym.entity.MembershipEntity;
import com.sigma.gym.exceptions.ValidationException;
import com.sigma.gym.repository.FreezeRulesRepository;
import com.sigma.gym.repository.MembershipRepository;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreezeService {
    
    private final FreezeRulesRepository freezeRulesRepository;
    private final MembershipRepository membershipRepository;
    
    public FreezeRulesDTO getFreezeRules(String userEmail) {
        FreezeRulesEntity rules = freezeRulesRepository.findById(1L)
            .orElseThrow(() -> new ValidationException("Reglas de congelamiento no configuradas"));
        
        MembershipEntity membership = membershipRepository.findByUserEmail(userEmail)
            .orElse(null);
        
        int remainingFreezes = 0;
        if (membership != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            LocalDateTime membershipYear = membership.getCreatedAt();
            
            // Si la membresía es del año actual, calcular congelamientos restantes
            if (membershipYear.getYear() == currentYear) {
                remainingFreezes = Math.max(0, rules.getMaxFreezesPerYear() - membership.getFreezesUsedThisYear());
            } else {
                // Si es de un año diferente, resetear contador (esto se debería hacer con scheduler)
                remainingFreezes = rules.getMaxFreezesPerYear();
            }
        }
        
        return FreezeRulesDTO.builder()
            .minDays(rules.getMinDays())
            .maxDays(rules.getMaxDays())
            .maxFreezesPerYear(rules.getMaxFreezesPerYear())
            .advanceNoticeDays(rules.getAdvanceNoticeDays())
            .remainingFreezes(remainingFreezes)
            .build();
    }
    
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public FreezeResponseDTO freezeMembership(String userEmail, FreezeRequestDTO request) {
        // Obtener reglas
        FreezeRulesEntity rules = freezeRulesRepository.findById(1L)
            .orElseThrow(() -> new ValidationException("Reglas de congelamiento no configuradas"));
        
        // Obtener membresía con lock
        MembershipEntity membership = membershipRepository.findByUserEmailForUpdate(userEmail)
            .orElseThrow(() -> new ValidationException("No se encontró membresía activa"));
        
        // Validar estado
        if (membership.getStatus() != MembershipEntity.MembershipStatus.ACTIVE) {
            throw new ValidationException("Solo se pueden congelar membresías activas");
        }
        
        // Validar que no haya congelamiento activo
        if (membership.getFreezeStart() != null && membership.getFreezeEnd() != null) {
            LocalDate today = LocalDate.now();
            LocalDate freezeStart = membership.getFreezeStart().toLocalDate();
            LocalDate freezeEnd = membership.getFreezeEnd().toLocalDate();
            
            if ((today.isAfter(freezeStart) || today.isEqual(freezeStart)) && 
                (today.isBefore(freezeEnd) || today.isEqual(freezeEnd))) {
                throw new ValidationException("Ya existe un congelamiento activo", 409);
            }
        }
        
        // Validar fechas
        validateFreezeRequest(request, rules);
        
        // Validar cupo de congelamientos
        if (membership.getFreezesUsedThisYear() >= rules.getMaxFreezesPerYear()) {
            throw new ValidationException("No hay congelamientos disponibles para este año");
        }
        
        // Verificar idempotencia
        if (membership.getFreezeStart() != null && membership.getFreezeEnd() != null) {
            LocalDate existingStart = membership.getFreezeStart().toLocalDate();
            LocalDate existingEnd = membership.getFreezeEnd().toLocalDate();
            
            if (existingStart.equals(request.getStartDate()) && existingEnd.equals(request.getEndDate())) {
                // Mismo rango, devolver respuesta idempotente
                return buildFreezeResponse(membership);
            }
        }
        
        // Aplicar congelamiento
        membership.setStatus(MembershipEntity.MembershipStatus.FROZEN);
        membership.setFreezeStart(request.getStartDate().atStartOfDay());
        membership.setFreezeEnd(request.getEndDate().atStartOfDay());
        membership.setFreezesUsedThisYear(membership.getFreezesUsedThisYear() + 1);
        
        // Calcular extensión de billing date
        long durationDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        if (membership.getNextBillingDate() != null) {
            membership.setNextBillingDate(membership.getNextBillingDate().plusDays(durationDays));
        } else {
            // Si no existe, calcular sobre created_at + periodo del plan
            LocalDateTime originalBillingDate = membership.getCreatedAt().plusDays(membership.getPlan().getDurationDays());
            membership.setNextBillingDate(originalBillingDate.plusDays(durationDays));
        }
        
        membershipRepository.save(membership);
        
        // Log de auditoría
        log.info("Freeze applied: userId={}, from_status=ACTIVE, to_status=FROZEN, freeze_start={}, freeze_end={}", 
                 membership.getUser().getId(), request.getStartDate(), request.getEndDate());
        
        return buildFreezeResponse(membership);
    }
    
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public UnfreezeResponseDTO unfreezeMembership(String userEmail) {
        MembershipEntity membership = membershipRepository.findByUserEmailForUpdate(userEmail)
            .orElseThrow(() -> new ValidationException("No se encontró membresía"));
        
        if (membership.getStatus() != MembershipEntity.MembershipStatus.FROZEN) {
            throw new ValidationException("La membresía no está congelada");
        }
        
        // Calcular días realmente consumidos
        LocalDate today = LocalDate.now();
        LocalDate freezeStart = membership.getFreezeStart().toLocalDate();
        LocalDate freezeEnd = membership.getFreezeEnd().toLocalDate();
        
        LocalDate actualEnd = today.isBefore(freezeEnd) ? today : freezeEnd;
        long actualDaysUsed = ChronoUnit.DAYS.between(freezeStart, actualEnd) + 1;
        long plannedDays = ChronoUnit.DAYS.between(freezeStart, freezeEnd) + 1;
        
        // Ajustar extensión si se reactiva antes
        if (actualDaysUsed < plannedDays) {
            long daysToReduce = plannedDays - actualDaysUsed;
            if (membership.getNextBillingDate() != null) {
                membership.setNextBillingDate(membership.getNextBillingDate().minusDays(daysToReduce));
            }
        }
        
        // Activar membresía
        membership.setStatus(MembershipEntity.MembershipStatus.ACTIVE);
        membership.setFreezeStart(null);
        membership.setFreezeEnd(null);
        
        membershipRepository.save(membership);
        
        // Log de auditoría
        log.info("Unfreeze applied: userId={}, from_status=FROZEN, to_status=ACTIVE, days_consumed={}", 
                 membership.getUser().getId(), actualDaysUsed);
        
        return UnfreezeResponseDTO.builder()
            .status("ACTIVE")
            .nextBillingDate(membership.getNextBillingDate() != null ? 
                membership.getNextBillingDate().toLocalDate().toString() : null)
            .build();
    }
    
    private void validateFreezeRequest(FreezeRequestDTO request, FreezeRulesEntity rules) {
        LocalDate today = LocalDate.now();
        LocalDate minStartDate = today.plusDays(rules.getAdvanceNoticeDays());
        
        // Validar fecha de inicio
        if (request.getStartDate().isBefore(minStartDate)) {
            throw new ValidationException("La fecha de inicio debe ser al menos " + rules.getAdvanceNoticeDays() + " días en el futuro");
        }
        
        // Validar que end sea después de start
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new ValidationException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        // Validar duración
        long durationDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        
        if (durationDays < rules.getMinDays()) {
            throw new ValidationException("El congelamiento debe ser de al menos " + rules.getMinDays() + " días");
        }
        
        if (durationDays > rules.getMaxDays()) {
            throw new ValidationException("El congelamiento no puede ser mayor a " + rules.getMaxDays() + " días");
        }
    }
    
    private FreezeResponseDTO buildFreezeResponse(MembershipEntity membership) {
        return FreezeResponseDTO.builder()
            .status("FROZEN")
            .freeze(FreezeDTO.builder()
                .startDate(membership.getFreezeStart().toLocalDate().toString())
                .endDate(membership.getFreezeEnd().toLocalDate().toString())
                .build())
            .nextBillingDate(membership.getNextBillingDate() != null ?
                membership.getNextBillingDate().toLocalDate().toString() : null)
            .build();
    }
}
