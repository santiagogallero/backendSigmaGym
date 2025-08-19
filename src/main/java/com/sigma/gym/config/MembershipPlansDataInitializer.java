package com.sigma.gym.config;

import com.sigma.gym.entity.MembershipPlanEntity;
import com.sigma.gym.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3) // Se ejecuta después de DataInitializer
public class MembershipPlansDataInitializer implements CommandLineRunner {
    
    private final MembershipPlanRepository membershipPlanRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (membershipPlanRepository.count() == 0) {
            initializeMembershipPlans();
        }
    }
    
    private void initializeMembershipPlans() {
        log.info("Initializing membership plans data...");
        
        List<MembershipPlanEntity> plans = List.of(
            MembershipPlanEntity.builder()
                .name("Plan Mensual")
                .description("Membresía básica con acceso completo al gimnasio por 30 días")
                .price(new BigDecimal("25000"))
                .currency("ARS")
                .durationDays(30)
                .active(true)
                .build(),
                
            MembershipPlanEntity.builder()
                .name("Plan Trimestral")
                .description("Membresía por 3 meses con descuento del 15%")
                .price(new BigDecimal("63750")) // 25000 * 3 * 0.85
                .currency("ARS")
                .durationDays(90)
                .active(true)
                .build(),
                
            MembershipPlanEntity.builder()
                .name("Plan Semestral")
                .description("Membresía por 6 meses con descuento del 20%")
                .price(new BigDecimal("120000")) // 25000 * 6 * 0.80
                .currency("ARS")
                .durationDays(180)
                .active(true)
                .build(),
                
            MembershipPlanEntity.builder()
                .name("Plan Anual")
                .description("Membresía por 12 meses con descuento del 25%")
                .price(new BigDecimal("225000")) // 25000 * 12 * 0.75
                .currency("ARS")
                .durationDays(365)
                .active(true)
                .build()
        );
        
        membershipPlanRepository.saveAll(plans);
        log.info("Membership plans initialized successfully. Created {} plans", plans.size());
    }
}
