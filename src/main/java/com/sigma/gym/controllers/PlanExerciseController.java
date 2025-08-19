package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.PlanExerciseDTO;
import com.sigma.gym.entity.PlanExerciseEntity;
import com.sigma.gym.mappers.PlanExerciseMapper;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.PlanExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing plan exercises with drag & drop support
 */
@RestController
@RequestMapping("/api/v2/plan-exercises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlanExerciseController {

    private final PlanExerciseService planExerciseService;

    /**
     * Create a new plan exercise
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanExerciseDTO>> createPlanExercise(
            @Valid @RequestBody PlanExerciseDTO planExerciseDTO,
            @RequestParam Long dayId) {
        
        PlanExerciseEntity created = planExerciseService.createPlanExercise(dayId, planExerciseDTO);
        PlanExerciseDTO responseDTO = PlanExerciseMapper.toDTO(created);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.ok("Plan exercise created successfully", responseDTO));
    }

    /**
     * Get all exercises for a plan day
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<List<PlanExerciseDTO>>> getPlanExercisesByDayId(
            @RequestParam Long dayId,
            @RequestParam(required = false) String type) {
        
        List<PlanExerciseEntity> exercises;
        
        if ("warmup".equals(type)) {
            exercises = planExerciseService.getWarmupExercises(dayId);
        } else if ("main".equals(type)) {
            exercises = planExerciseService.getMainExercises(dayId);
        } else {
            exercises = planExerciseService.getPlanExercisesByDayId(dayId);
        }
        
        List<PlanExerciseDTO> responseDTOs = PlanExerciseMapper.toDTOList(exercises);
        
        return ResponseEntity.ok(ResponseData.ok("Plan exercises retrieved successfully", responseDTOs));
    }

    /**
     * Get a specific plan exercise by ID
     */
    @GetMapping("/{exerciseId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanExerciseDTO>> getPlanExerciseById(
            @PathVariable Long exerciseId) {
        
        Optional<PlanExerciseEntity> exercise = planExerciseService.getPlanExerciseById(exerciseId);
        
        if (exercise.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Plan exercise not found"));
        }
        
        PlanExerciseDTO responseDTO = PlanExerciseMapper.toDTO(exercise.get());
        
        return ResponseEntity.ok(ResponseData.ok("Plan exercise retrieved successfully", responseDTO));
    }

    /**
     * Update a plan exercise
     */
    @PutMapping("/{exerciseId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanExerciseDTO>> updatePlanExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody PlanExerciseDTO planExerciseDTO) {
        
        try {
            PlanExerciseEntity updated = planExerciseService.updatePlanExercise(exerciseId, planExerciseDTO);
            PlanExerciseDTO responseDTO = PlanExerciseMapper.toDTO(updated);
            
            return ResponseEntity.ok(ResponseData.ok("Plan exercise updated successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Delete a plan exercise
     */
    @DeleteMapping("/{exerciseId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> deletePlanExercise(
            @PathVariable Long exerciseId) {
        
        try {
            planExerciseService.deletePlanExercise(exerciseId);
            return ResponseEntity.ok(ResponseData.ok("Plan exercise deleted successfully", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Reorder a plan exercise (drag & drop)
     */
    @PostMapping("/reorder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> reorderPlanExercise(
            @RequestParam Long dayId,
            @RequestParam int fromIndex,
            @RequestParam int toIndex) {
        
        try {
            planExerciseService.reorderPlanExercise(dayId, fromIndex, toIndex);
            return ResponseEntity.ok(ResponseData.ok("Plan exercise reordered successfully", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Insert a plan exercise at a specific position
     */
    @PostMapping("/insert")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanExerciseDTO>> insertPlanExerciseAt(
            @RequestParam Long dayId,
            @RequestParam int position,
            @Valid @RequestBody PlanExerciseDTO planExerciseDTO) {
        
        try {
            PlanExerciseEntity inserted = planExerciseService.insertPlanExerciseAt(dayId, position, planExerciseDTO);
            PlanExerciseDTO responseDTO = PlanExerciseMapper.toDTO(inserted);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.ok("Plan exercise inserted successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Search exercises by name within a day
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<List<PlanExerciseDTO>>> searchExercisesByName(
            @RequestParam Long dayId,
            @RequestParam String name) {
        
        List<PlanExerciseEntity> exercises = planExerciseService.searchExercisesByName(dayId, name);
        List<PlanExerciseDTO> responseDTOs = PlanExerciseMapper.toDTOList(exercises);
        
        return ResponseEntity.ok(ResponseData.ok("Exercises found successfully", responseDTOs));
    }

    /**
     * Get the next available order index for a day
     */
    @GetMapping("/next-order-index")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<Integer>> getNextOrderIndex(
            @RequestParam Long dayId) {
        
        int nextIndex = planExerciseService.getNextOrderIndex(dayId);
        
        return ResponseEntity.ok(ResponseData.ok("Next order index retrieved successfully", nextIndex));
    }

    /**
     * Validate and fix order indexes for a day
     */
    @PostMapping("/validate-order")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> validateAndFixOrder(
            @RequestParam Long dayId) {
        
        planExerciseService.validateAndFixOrder(dayId);
        
        return ResponseEntity.ok(ResponseData.ok("Plan exercise order validated and fixed successfully", "OK"));
    }
}
