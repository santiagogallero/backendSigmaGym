package com.sigma.gym.repository;

import com.sigma.gym.entity.PlanPriceTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanPriceTierRepository extends JpaRepository<PlanPriceTierEntity, Long> {
    
    /**
     * Find all price tiers for a plan
     */
    List<PlanPriceTierEntity> findByPlanIdOrderByTimesPerWeekAsc(Long planId);
    
    /**
     * Delete all price tiers for a plan
     */
    void deleteByPlanId(Long planId);
}
