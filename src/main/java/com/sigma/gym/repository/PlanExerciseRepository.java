package com.sigma.gym.repository;

import com.sigma.gym.entity.PlanExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanExerciseRepository extends JpaRepository<PlanExerciseEntity, Long> {
    
    /**
     * Find all exercises for a specific plan day, ordered by orderIndex
     */
    List<PlanExerciseEntity> findByPlanDay_IdOrderByOrderIndexAsc(Long dayId);
    
    /**
     * Find a specific exercise by day ID and order index
     */
    Optional<PlanExerciseEntity> findByPlanDay_IdAndOrderIndex(Long dayId, Integer orderIndex);
    
    /**
     * Check if an exercise exists at a specific order index for a day
     */
    boolean existsByPlanDay_IdAndOrderIndex(Long dayId, Integer orderIndex);
    
    /**
     * Count total exercises in a day
     */
    int countByPlanDay_Id(Long dayId);
    
    /**
     * Find exercises with order index greater than or equal to specified value
     */
    List<PlanExerciseEntity> findByPlanDay_IdAndOrderIndexGreaterThanEqualOrderByOrderIndexAsc(Long dayId, Integer orderIndex);
    
    /**
     * Find warmup exercises for a day
     */
    List<PlanExerciseEntity> findByPlanDay_IdAndIsWarmupTrueOrderByOrderIndexAsc(Long dayId);
    
    /**
     * Find main exercises (non-warmup) for a day
     */
    List<PlanExerciseEntity> findByPlanDay_IdAndIsWarmupFalseOrderByOrderIndexAsc(Long dayId);
    
    /**
     * Update order indexes for reordering operations
     * Increment order indexes by 1 for exercises >= specified order index
     */
    @Modifying
    @Query("UPDATE PlanExerciseEntity e SET e.orderIndex = e.orderIndex + 1 WHERE e.planDay.id = :dayId AND e.orderIndex >= :fromIndex")
    void incrementOrderIndexesFrom(@Param("dayId") Long dayId, @Param("fromIndex") Integer fromIndex);
    
    /**
     * Decrement order indexes by 1 for exercises >= specified order index
     */
    @Modifying
    @Query("UPDATE PlanExerciseEntity e SET e.orderIndex = e.orderIndex - 1 WHERE e.planDay.id = :dayId AND e.orderIndex >= :fromIndex")
    void decrementOrderIndexesFrom(@Param("dayId") Long dayId, @Param("fromIndex") Integer fromIndex);
    
    /**
     * Update order indexes for range reordering
     */
    @Modifying
    @Query("UPDATE PlanExerciseEntity e SET e.orderIndex = e.orderIndex + :offset WHERE e.planDay.id = :dayId AND e.orderIndex BETWEEN :startIndex AND :endIndex")
    void updateOrderIndexRange(@Param("dayId") Long dayId, @Param("startIndex") Integer startIndex, 
                               @Param("endIndex") Integer endIndex, @Param("offset") Integer offset);
    
    /**
     * Get the maximum order index for a day (for adding new exercises)
     */
    @Query("SELECT COALESCE(MAX(e.orderIndex), -1) FROM PlanExerciseEntity e WHERE e.planDay.id = :dayId")
    Integer findMaxOrderIndexByDayId(@Param("dayId") Long dayId);
    
    /**
     * Delete all exercises for a specific day
     */
    void deleteByPlanDay_Id(Long dayId);
    
    /**
     * Find exercises by day ID and name (case-insensitive)
     */
    List<PlanExerciseEntity> findByPlanDay_IdAndNameContainingIgnoreCase(Long dayId, String name);
    
    /**
     * Count warmup exercises in a day
     */
    int countByPlanDay_IdAndIsWarmupTrue(Long dayId);
    
    /**
     * Count main exercises in a day
     */
    int countByPlanDay_IdAndIsWarmupFalse(Long dayId);
}
