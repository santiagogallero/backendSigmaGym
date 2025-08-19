package com.sigma.gym.services;

import com.sigma.gym.DTOs.MembershipStatusContractDTO;
import com.sigma.gym.DTOs.MembershipStatusDTO;
import com.sigma.gym.DTOs.FreezeWindowDTO;
import com.sigma.gym.entity.MembershipEntity;
import com.sigma.gym.entity.MembershipPlanEntity;
import com.sigma.gym.entity.PaymentLogEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.MembershipRepository;
import com.sigma.gym.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {
    
    private final MembershipRepository membershipRepository;
    private final PaymentLogRepository paymentLogRepository;
    
    public MembershipStatusDTO getUserMembershipStatus(UserEntity user) {
        Optional<MembershipEntity> membershipOpt = membershipRepository.findByUser(user);
        return MembershipStatusDTO.fromEntity(membershipOpt.orElse(null));
    }
    
    public MembershipStatusContractDTO getUserMembershipStatusContract(UserEntity user) {
        Optional<MembershipEntity> membershipOpt = membershipRepository.findByUser(user);
        
        if (membershipOpt.isEmpty()) {
            return MembershipStatusContractDTO.builder()
                .status("NONE")
                .build();
        }
        
        MembershipEntity membership = membershipOpt.get();
        MembershipStatusContractDTO.MembershipStatusContractDTOBuilder builder = MembershipStatusContractDTO.builder()
            .status(membership.getStatus().name());
        
        // Agregar plan si existe
        if (membership.getPlan() != null) {
            builder.plan(MembershipStatusContractDTO.PlanSummaryDTO.builder()
                .id(membership.getPlan().getId())
                .name(membership.getPlan().getName())
                .price(membership.getPlan().getPrice().doubleValue())
                .currency(membership.getPlan().getCurrency())
                .build());
        }
        
        // Agregar nextBillingDate si existe
        if (membership.getNextBillingDate() != null) {
            builder.nextBillingDate(membership.getNextBillingDate().toLocalDate().toString());
        }
        
        // Agregar freezeWindow si está congelado
        if (membership.getStatus() == MembershipEntity.MembershipStatus.FROZEN &&
            membership.getFreezeStart() != null && membership.getFreezeEnd() != null) {
            builder.freezeWindow(FreezeWindowDTO.builder()
                .startDate(membership.getFreezeStart().toLocalDate().toString())
                .endDate(membership.getFreezeEnd().toLocalDate().toString())
                .build());
        }
        
        return builder.build();
    }
    
    @Transactional
    public void activateMembership(String externalReference, String mpPaymentId) {
        Optional<MembershipEntity> membershipOpt = membershipRepository.findByExternalReference(externalReference);
        
        if (membershipOpt.isEmpty()) {
            log.error("Membership not found for external reference: {}", externalReference);
            return;
        }
        
        MembershipEntity membership = membershipOpt.get();
        
        if (membership.getStatus() != MembershipEntity.MembershipStatus.PENDING) {
            log.warn("Membership is not in PENDING status. Current status: {}", membership.getStatus());
            return;
        }
        
        // Cancelar cualquier membresía activa anterior del mismo usuario
        membershipRepository.findByUser(membership.getUser())
            .stream()
            .filter(m -> !m.getId().equals(membership.getId()) && 
                        m.getStatus() == MembershipEntity.MembershipStatus.ACTIVE)
            .forEach(m -> {
                m.setStatus(MembershipEntity.MembershipStatus.CANCELED);
                membershipRepository.save(m);
                log.info("Canceled previous active membership: {}", m.getId());
            });
        
        // Activar la nueva membresía
        MembershipPlanEntity plan = membership.getPlan();
        LocalDateTime now = LocalDateTime.now();
        
        membership.setStatus(MembershipEntity.MembershipStatus.ACTIVE);
        membership.setStartDate(now);
        membership.setExpirationDate(now.plusDays(plan.getDurationDays()));
        membership.setLastPaymentId(mpPaymentId);
        
        membershipRepository.save(membership);
        
        log.info("Membership activated successfully. ID: {}, User: {}, Expires: {}", 
                 membership.getId(), membership.getUser().getId(), membership.getExpirationDate());
    }
    
    @Transactional
    public void cancelMembership(String externalReference) {
        Optional<MembershipEntity> membershipOpt = membershipRepository.findByExternalReference(externalReference);
        
        if (membershipOpt.isEmpty()) {
            log.error("Membership not found for external reference: {}", externalReference);
            return;
        }
        
        MembershipEntity membership = membershipOpt.get();
        membership.setStatus(MembershipEntity.MembershipStatus.CANCELED);
        membershipRepository.save(membership);
        
        log.info("Membership canceled. ID: {}, User: {}", 
                 membership.getId(), membership.getUser().getId());
    }
    
    @Transactional
    public void cancelUserMembership(UserEntity user) {
        Optional<MembershipEntity> membershipOpt = membershipRepository.findByUser(user);
        
        if (membershipOpt.isEmpty()) {
            log.warn("No membership found for user: {}", user.getId());
            return;
        }
        
        MembershipEntity membership = membershipOpt.get();
        
        if (membership.getStatus() == MembershipEntity.MembershipStatus.ACTIVE ||
            membership.getStatus() == MembershipEntity.MembershipStatus.PENDING) {
            
            membership.setStatus(MembershipEntity.MembershipStatus.CANCELED);
            membershipRepository.save(membership);
            
            log.info("User membership canceled. ID: {}, User: {}", 
                     membership.getId(), user.getId());
        }
    }
    
    /**
     * Tarea programada para expirar membresías vencidas
     * Se ejecuta cada hora
     */
    @Scheduled(fixedRate = 3600000) // 1 hora en millisegundos
    @Transactional
    public void expireOldMemberships() {
        LocalDateTime now = LocalDateTime.now();
        List<MembershipEntity> expiredMemberships = membershipRepository.findExpiredActiveMemberships(now);
        
        if (!expiredMemberships.isEmpty()) {
            log.info("Found {} expired memberships to process", expiredMemberships.size());
            
            for (MembershipEntity membership : expiredMemberships) {
                // No expirar membresías congeladas
                if (membership.getStatus() != MembershipEntity.MembershipStatus.FROZEN) {
                    membership.setStatus(MembershipEntity.MembershipStatus.EXPIRED);
                    membershipRepository.save(membership);
                    
                    log.info("Membership expired. ID: {}, User: {}, Expired on: {}", 
                             membership.getId(), membership.getUser().getId(), membership.getExpirationDate());
                }
            }
        }
    }
    
    /**
     * Tarea programada para descongelar membresías automáticamente
     * Se ejecuta diariamente a las 00:00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processAutomaticUnfreeze() {
        LocalDateTime now = LocalDateTime.now();
        List<MembershipEntity> frozenMembershipsToUnfreeze = membershipRepository.findFrozenMembershipsToUnfreeze(now);
        
        if (!frozenMembershipsToUnfreeze.isEmpty()) {
            log.info("Found {} frozen memberships to unfreeze automatically", frozenMembershipsToUnfreeze.size());
            
            for (MembershipEntity membership : frozenMembershipsToUnfreeze) {
                // Calcular días realmente consumidos
                LocalDate freezeStart = membership.getFreezeStart().toLocalDate();
                LocalDate freezeEnd = membership.getFreezeEnd().toLocalDate();
                LocalDate today = LocalDate.now();
                
                LocalDate actualEnd = today.isBefore(freezeEnd) ? today : freezeEnd;
                long actualDaysUsed = ChronoUnit.DAYS.between(freezeStart, actualEnd) + 1;
                
                // Activar membresía
                membership.setStatus(MembershipEntity.MembershipStatus.ACTIVE);
                membership.setFreezeStart(null);
                membership.setFreezeEnd(null);
                
                membershipRepository.save(membership);
                
                log.info("Automatic unfreeze completed: userId={}, from_status=FROZEN, to_status=ACTIVE, days_consumed={}", 
                         membership.getUser().getId(), actualDaysUsed);
            }
        }
    }
    
    public long getActiveMembershipsCount() {
        return membershipRepository.countActiveMemberships();
    }
    
    public boolean hasActiveMembership(UserEntity user) {
        Optional<MembershipEntity> membershipOpt = membershipRepository.findByUser(user);
        return membershipOpt.isPresent() && 
               (membershipOpt.get().getStatus() == MembershipEntity.MembershipStatus.ACTIVE ||
                membershipOpt.get().getStatus() == MembershipEntity.MembershipStatus.FROZEN) &&
               membershipOpt.get().getExpirationDate().isAfter(LocalDateTime.now());
    }
}
