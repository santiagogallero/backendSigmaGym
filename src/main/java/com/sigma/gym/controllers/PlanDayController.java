package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.PlanDayDTO;
import com.sigma.gym.entity.PlanDayEntity;
import com.sigma.gym.mappers.PlanDayMapper;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.PlanDayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing plan days with drag & drop support
 */
@RestController
@RequestMapping("/api/v2/plan-days")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlanDayController {

    private final PlanDayService planDayService;

    /**
     * Create a new plan day
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanDayDTO>> createPlanDay(
            @Valid @RequestBody PlanDayDTO planDayDTO,
            @RequestParam Long planId) {
        
        PlanDayEntity created = planDayService.createPlanDay(planId, planDayDTO);
        PlanDayDTO responseDTO = PlanDayMapper.toDTO(created);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.ok("Plan day created successfully", responseDTO));
    }

    /**
     * Get all days for a workout plan
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<List<PlanDayDTO>>> getPlanDaysByPlanId(
            @RequestParam Long planId) {
        
        List<PlanDayEntity> planDays = planDayService.getPlanDaysByPlanId(planId);
        List<PlanDayDTO> responseDTOs = PlanDayMapper.toDTOList(planDays);
        
        return ResponseEntity.ok(ResponseData.ok("Plan days retrieved successfully", responseDTOs));
    }

    /**
     * Get a specific plan day by ID
     */
    @GetMapping("/{dayId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanDayDTO>> getPlanDayById(
            @PathVariable Long dayId) {
        
        Optional<PlanDayEntity> planDay = planDayService.getPlanDayById(dayId);
        
        if (planDay.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.error("Plan day not found"));
        }
        
        PlanDayDTO responseDTO = PlanDayMapper.toDTO(planDay.get());
        
        return ResponseEntity.ok(ResponseData.ok("Plan day retrieved successfully", responseDTO));
    }

    /**
     * Update a plan day
     */
    @PutMapping("/{dayId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanDayDTO>> updatePlanDay(
            @PathVariable Long dayId,
            @Valid @RequestBody PlanDayDTO planDayDTO) {
        
        try {
            PlanDayEntity updated = planDayService.updatePlanDay(dayId, planDayDTO);
            PlanDayDTO responseDTO = PlanDayMapper.toDTO(updated);
            
            return ResponseEntity.ok(ResponseData.ok("Plan day updated successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Delete a plan day
     */
    @DeleteMapping("/{dayId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> deletePlanDay(
            @PathVariable Long dayId) {
        
        try {
            planDayService.deletePlanDay(dayId);
            return ResponseEntity.ok(ResponseData.ok("Plan day deleted successfully", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Reorder a plan day (drag & drop)
     */
    @PostMapping("/reorder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> reorderPlanDay(
            @RequestParam Long planId,
            @RequestParam int fromIndex,
            @RequestParam int toIndex) {
        
        try {
            planDayService.reorderPlanDay(planId, fromIndex, toIndex);
            return ResponseEntity.ok(ResponseData.ok("Plan day reordered successfully", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Insert a plan day at a specific position
     */
    @PostMapping("/insert")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<PlanDayDTO>> insertPlanDayAt(
            @RequestParam Long planId,
            @RequestParam int position,
            @Valid @RequestBody PlanDayDTO planDayDTO) {
        
        try {
            PlanDayEntity inserted = planDayService.insertPlanDayAt(planId, position, planDayDTO);
            PlanDayDTO responseDTO = PlanDayMapper.toDTO(inserted);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.ok("Plan day inserted successfully", responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        }
    }

    /**
     * Get the next available order index for a plan
     */
    @GetMapping("/next-order-index")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<Integer>> getNextOrderIndex(
            @RequestParam Long planId) {
        
        int nextIndex = planDayService.getNextOrderIndex(planId);
        
        return ResponseEntity.ok(ResponseData.ok("Next order index retrieved successfully", nextIndex));
    }

    /**
     * Validate and fix order indexes for a plan
     */
    @PostMapping("/validate-order")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData<String>> validateAndFixOrder(
            @RequestParam Long planId) {
        
        planDayService.validateAndFixOrder(planId);
        
        return ResponseEntity.ok(ResponseData.ok("Plan day order validated and fixed successfully", "OK"));
    }
}
