package com.sigma.gym.repository;

import com.sigma.gym.entity.PlansSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlansSettingsRepository extends JpaRepository<PlansSettingsEntity, Long> {
    
    /**
     * Get the singleton settings record
     */
    default Optional<PlansSettingsEntity> getSettings() {
        return findById(1L);
    }
}
