package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.WorkoutPlanDTO;
import com.sigma.gym.DTOs.WorkoutPlanStatsDTO;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.mappers.WorkoutPlanEntityMapper;
import com.sigma.gym.model.WorkoutPlanStatus;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Enhanced REST controller for Workout Plan management with slug support
 */
@RestController
@RequestMapping("/api/v2/workout-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkoutPlanV2Controller {

    private final WorkoutPlanService workoutPlanService;

    /**
     * Create a new workout plan
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<WorkoutPlanDTO>> createWorkoutPlan(
            @Valid @RequestBody WorkoutPlanDTO workoutPlanDTO,
            @RequestParam Long ownerId) {
        
        WorkoutPlanEntity created = workoutPlanService.createWorkoutPlanEntity(ownerId, workoutPlanDTO);
        WorkoutPlanDTO responseDTO = WorkoutPlanEntityMapper.toDTO(created);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.success("Workout plan created successfully", responseDTO));
    }

    /**
     * Get workout plan by slug
     */
    @GetMapping("/{slug}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<WorkoutPlanDTO>> getWorkoutPlanBySlug(
            @PathVariable String slug,
            @RequestParam Long ownerId) {
        
        Optional<WorkoutPlanEntity> workoutPlan = workoutPlanService.getWorkoutPlanBySlug(ownerId, slug);
        
        if (workoutPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Workout plan not found"));
        }
        
        WorkoutPlanDTO responseDTO = WorkoutPlanEntityMapper.toDTO(workoutPlan.get());
        
        return ResponseEntity.ok(ResponseData.success("Workout plan retrieved successfully", responseDTO));
    }

    /**
     * Get workout plan by ID (for backward compatibility)
     */
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<WorkoutPlanDTO>> getWorkoutPlanById(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        Optional<WorkoutPlanEntity> workoutPlan = workoutPlanService.getWorkoutPlanEntityById(id);
        
        if (workoutPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Workout plan not found"));
        }
        
        // Check ownership
        if (!workoutPlan.get().getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseData.error("Access denied"));
        }
        
        WorkoutPlanDTO responseDTO = WorkoutPlanEntityMapper.toDTO(workoutPlan.get());
        
        return ResponseEntity.ok(ResponseData.success("Workout plan retrieved successfully", responseDTO));
    }

    /**
     * Get all workout plans for an owner
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<List<WorkoutPlanDTO>>> getAllWorkoutPlans(
            @RequestParam Long ownerId,
            @RequestParam(required = false) WorkoutPlanStatus status,
            @RequestParam(required = false) String search) {
        
        List<WorkoutPlanEntity> workoutPlans;
        
        if (search != null && !search.trim().isEmpty()) {
            workoutPlans = workoutPlanService.searchWorkoutPlansByName(ownerId, search.trim());
        } else if (status != null) {
            workoutPlans = workoutPlanService.getWorkoutPlansByOwnerAndStatus(ownerId, status);
        } else {
            workoutPlans = workoutPlanService.getWorkoutPlansByOwnerId(ownerId);
        }
        
        List<WorkoutPlanDTO> responseDTOs = WorkoutPlanEntityMapper.toSummaryDTOList(workoutPlans);
        
        return ResponseEntity.ok(ResponseData.success("Workout plans retrieved successfully", responseDTOs));
    }

    /**
     * Update workout plan
     */
    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<WorkoutPlanDTO>> updateWorkoutPlan(
            @PathVariable String slug,
            @Valid @RequestBody WorkoutPlanDTO workoutPlanDTO,
            @RequestParam Long ownerId) {
        
        Optional<WorkoutPlanEntity> existingPlan = workoutPlanService.getWorkoutPlanBySlug(ownerId, slug);
        
        if (existingPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Workout plan not found"));
        }
        
        if (!workoutPlanService.canEditWorkoutPlan(existingPlan.get().getId(), ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseData.error("Cannot edit active or archived workout plans"));
        }
        
        WorkoutPlanEntity updated = workoutPlanService.updateWorkoutPlanEntity(existingPlan.get().getId(), workoutPlanDTO);
        WorkoutPlanDTO responseDTO = WorkoutPlanEntityMapper.toDTO(updated);
        
        return ResponseEntity.ok(ResponseData.success("Workout plan updated successfully", responseDTO));
    }

    /**
     * Change workout plan status
     */
    @PatchMapping("/{slug}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<WorkoutPlanDTO>> changeWorkoutPlanStatus(
            @PathVariable String slug,
            @RequestParam WorkoutPlanStatus status,
            @RequestParam Long ownerId) {
        
        Optional<WorkoutPlanEntity> existingPlan = workoutPlanService.getWorkoutPlanBySlug(ownerId, slug);
        
        if (existingPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Workout plan not found"));
        }
        
        WorkoutPlanEntity updated = workoutPlanService.changeStatus(existingPlan.get().getId(), status);
        WorkoutPlanDTO responseDTO = WorkoutPlanEntityMapper.toDTO(updated);
        
        return ResponseEntity.ok(ResponseData.success("Workout plan status changed successfully", responseDTO));
    }

    /**
     * Get workout plan statistics
     */
    @GetMapping("/{slug}/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<WorkoutPlanStatsDTO>> getWorkoutPlanStats(
            @PathVariable String slug,
            @RequestParam Long ownerId) {
        
        Optional<WorkoutPlanEntity> existingPlan = workoutPlanService.getWorkoutPlanBySlug(ownerId, slug);
        
        if (existingPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Workout plan not found"));
        }
        
        WorkoutPlanStatsDTO stats = workoutPlanService.getWorkoutPlanStats(existingPlan.get().getId());
        
        return ResponseEntity.ok(ResponseData.success("Workout plan statistics retrieved successfully", stats));
    }

    /**
     * Delete workout plan
     */
    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> deleteWorkoutPlan(
            @PathVariable String slug,
            @RequestParam Long ownerId) {
        
        Optional<WorkoutPlanEntity> existingPlan = workoutPlanService.getWorkoutPlanBySlug(ownerId, slug);
        
        if (existingPlan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Workout plan not found"));
        }
        
        workoutPlanService.deleteWorkoutPlanEntity(existingPlan.get().getId());
        
        return ResponseEntity.ok(ResponseData.success("Workout plan deleted successfully", "OK"));
    }
}
