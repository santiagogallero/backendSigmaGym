package com.sigma.gym.repository;

import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.model.WorkoutPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlanEntity, Long> {
    
    /**
     * Find workout plan by owner ID and slug
     */
    Optional<WorkoutPlanEntity> findByOwner_IdAndSlug(Long ownerId, String slug);
    
    /**
     * Check if a workout plan exists with the given owner ID and slug
     */
    boolean existsByOwner_IdAndSlug(Long ownerId, String slug);
    
    /**
     * Check if a workout plan exists with the given owner ID and name (case-insensitive)
     * Used for slug generation to avoid conflicts
     */
    boolean existsByOwner_IdAndNameIgnoreCase(Long ownerId, String name);
    
    /**
     * Find all workout plans by owner ID
     */
    List<WorkoutPlanEntity> findByOwner_Id(Long ownerId);
    
    /**
     * Find all workout plans by owner ID and status
     */
    List<WorkoutPlanEntity> findByOwner_IdAndStatus(Long ownerId, WorkoutPlanStatus status);
    
    /**
     * Find all active workout plans by owner ID
     */
    List<WorkoutPlanEntity> findByOwner_IdAndStatusOrderByCreatedAtDesc(Long ownerId, WorkoutPlanStatus status);
    
    /**
     * Find workout plans by name containing (case-insensitive) and owner ID
     */
    List<WorkoutPlanEntity> findByOwner_IdAndNameContainingIgnoreCase(Long ownerId, String name);
    
    /**
     * Count workout plans by owner ID
     */
    int countByOwner_Id(Long ownerId);
    
    /**
     * Count workout plans by owner ID and status
     */
    int countByOwner_IdAndStatus(Long ownerId, WorkoutPlanStatus status);
    
    /**
     * Find workout plans with duplicate names for slug conflict resolution
     */
    @Query("SELECT w FROM WorkoutPlanEntity w WHERE w.owner.id = :ownerId AND LOWER(w.name) = LOWER(:name) ORDER BY w.createdAt")
    List<WorkoutPlanEntity> findDuplicateNamesByOwnerId(@Param("ownerId") Long ownerId, @Param("name") String name);
    
    /**
     * Find all slugs starting with the given base slug for a specific owner
     * Used for generating unique slugs with incremental suffixes
     */
    @Query("SELECT w.slug FROM WorkoutPlanEntity w WHERE w.owner.id = :ownerId AND w.slug LIKE :baseSlug% ORDER BY w.slug")
    List<String> findSimilarSlugsByOwnerId(@Param("ownerId") Long ownerId, @Param("baseSlug") String baseSlug);
    
    /**
     * Delete all workout plans by owner ID
     */
    void deleteByOwner_Id(Long ownerId);
}
