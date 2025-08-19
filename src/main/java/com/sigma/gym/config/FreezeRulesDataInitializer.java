package com.sigma.gym.config;

import com.sigma.gym.entity.FreezeRulesEntity;
import com.sigma.gym.repository.FreezeRulesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class FreezeRulesDataInitializer implements CommandLineRunner {
    
    private final FreezeRulesRepository freezeRulesRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (freezeRulesRepository.count() == 0) {
            log.info("Initializing freeze rules data...");
            
            FreezeRulesEntity rules = FreezeRulesEntity.builder()
                .id(1L)
                .minDays(7)
                .maxDays(60)
                .maxFreezesPerYear(2)
                .advanceNoticeDays(1)
                .build();
            
            freezeRulesRepository.save(rules);
            
            log.info("Freeze rules initialized successfully: min={}d, max={}d, max_per_year={}, advance_notice={}d", 
                     rules.getMinDays(), rules.getMaxDays(), rules.getMaxFreezesPerYear(), rules.getAdvanceNoticeDays());
        }
    }
}
