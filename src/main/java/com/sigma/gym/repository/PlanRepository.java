package com.sigma.gym.repository;

import com.sigma.gym.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {
    
    /**
     * Find all visible plans ordered by sortOrder
     */
    List<PlanEntity> findByVisibleTrueOrderBySortOrderAsc();
    
    /**
     * Find all plans ordered by sortOrder (for admin)
     */
    List<PlanEntity> findAllByOrderBySortOrderAsc();
    
    /**
     * Count visible plans
     */
    long countByVisibleTrue();
}
