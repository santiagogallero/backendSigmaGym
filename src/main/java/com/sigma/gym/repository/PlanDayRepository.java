package com.sigma.gym.repository;

import com.sigma.gym.entity.PlanDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanDayRepository extends JpaRepository<PlanDayEntity, Long> {
    
    /**
     * Find all days for a specific workout plan, ordered by orderIndex
     */
    List<PlanDayEntity> findByWorkoutPlanIdOrderByOrderIndexAsc(Long workoutPlanId);
    
    /**
     * Find a specific day by plan ID and order index
     */
    Optional<PlanDayEntity> findByWorkoutPlanIdAndOrderIndex(Long workoutPlanId, Integer orderIndex);
    
    /**
     * Check if a day exists at a specific order index for a plan
     */
    boolean existsByWorkoutPlanIdAndOrderIndex(Long workoutPlanId, Integer orderIndex);
    
    /**
     * Count total days in a plan
     */
    int countByWorkoutPlanId(Long workoutPlanId);
    
    /**
     * Find days with order index greater than or equal to specified value
     */
    List<PlanDayEntity> findByWorkoutPlanIdAndOrderIndexGreaterThanEqualOrderByOrderIndexAsc(Long workoutPlanId, Integer orderIndex);
    
    /**
     * Update order indexes for reordering operations
     * Increment order indexes by 1 for days >= specified order index
     */
    @Modifying
    @Query("UPDATE PlanDayEntity d SET d.orderIndex = d.orderIndex + 1 WHERE d.workoutPlan.id = :workoutPlanId AND d.orderIndex >= :fromIndex")
    void incrementOrderIndexesFrom(@Param("workoutPlanId") Long workoutPlanId, @Param("fromIndex") Integer fromIndex);
    
    /**
     * Decrement order indexes by 1 for days >= specified order index
     */
    @Modifying
    @Query("UPDATE PlanDayEntity d SET d.orderIndex = d.orderIndex - 1 WHERE d.workoutPlan.id = :workoutPlanId AND d.orderIndex >= :fromIndex")
    void decrementOrderIndexesFrom(@Param("workoutPlanId") Long workoutPlanId, @Param("fromIndex") Integer fromIndex);
    
    /**
     * Update order indexes for range reordering
     */
    @Modifying
    @Query("UPDATE PlanDayEntity d SET d.orderIndex = d.orderIndex + :offset WHERE d.workoutPlan.id = :workoutPlanId AND d.orderIndex BETWEEN :startIndex AND :endIndex")
    void updateOrderIndexRange(@Param("workoutPlanId") Long workoutPlanId, @Param("startIndex") Integer startIndex, 
                               @Param("endIndex") Integer endIndex, @Param("offset") Integer offset);
    
    /**
     * Get the maximum order index for a plan (for adding new days)
     */
    @Query("SELECT COALESCE(MAX(d.orderIndex), -1) FROM PlanDayEntity d WHERE d.workoutPlan.id = :workoutPlanId")
    Integer findMaxOrderIndexByWorkoutPlanId(@Param("workoutPlanId") Long workoutPlanId);
    
    /**
     * Delete all days for a specific plan
     */
    void deleteByWorkoutPlanId(Long workoutPlanId);
}
