package com.sigma.gym.controllers.admin;

import com.sigma.gym.DTOs.plans.CreatePlanRequestDTO;
import com.sigma.gym.DTOs.plans.PlanDTO;
import com.sigma.gym.DTOs.plans.PlansSettingsDTO;
import com.sigma.gym.DTOs.plans.UpdatePlanRequestDTO;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.PlanService;
import com.sigma.gym.services.PlansSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPlansController {
    
    private final PlanService planService;
    private final PlansSettingsService plansSettingsService;
    
    /**
     * Get all plans (visible and hidden) - Owner only
     */
    @GetMapping("/plans")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<List<PlanDTO>>> getAllPlans() {
        try {
            List<PlanDTO> plans = planService.getAllPlans();
            return ResponseEntity.ok(ResponseData.ok(plans));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ResponseData.error("Failed to retrieve plans"));
        }
    }
    
    /**
     * Get plan by ID - Owner only
     */
    @GetMapping("/plans/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<PlanDTO>> getPlanById(@PathVariable Long id) {
        try {
            PlanDTO plan = planService.getPlanById(id);
            return ResponseEntity.ok(ResponseData.ok(plan));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(ResponseData.error("Plan not found"));
        }
    }
    
    /**
     * Create new plan - Owner only
     */
    @PostMapping("/plans")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<PlanDTO>> createPlan(
            @Valid @RequestBody CreatePlanRequestDTO request) {
        try {
            PlanDTO plan = planService.createPlan(request);
            return ResponseEntity.status(201)
                    .body(ResponseData.ok(plan));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ResponseData.error("Failed to create plan: " + e.getMessage()));
        }
    }
    
    /**
     * Update plan - Owner only
     */
    @PutMapping("/plans/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<PlanDTO>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlanRequestDTO request) {
        try {
            PlanDTO plan = planService.updatePlan(id, request);
            return ResponseEntity.ok(ResponseData.ok(plan));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ResponseData.error("Failed to update plan: " + e.getMessage()));
        }
    }
    
    /**
     * Delete plan - Owner only
     */
    @DeleteMapping("/plans/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<String>> deletePlan(@PathVariable Long id) {
        try {
            planService.deletePlan(id);
            return ResponseEntity.ok(ResponseData.ok("Plan deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ResponseData.error("Failed to delete plan: " + e.getMessage()));
        }
    }
    
    /**
     * Get admin settings - Owner only
     */
    @GetMapping("/plans/settings")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<PlansSettingsDTO>> getAdminSettings() {
        try {
            PlansSettingsDTO settings = plansSettingsService.getSettings();
            return ResponseEntity.ok(ResponseData.ok(settings));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ResponseData.error("Failed to retrieve settings"));
        }
    }
    
    /**
     * Update settings - Owner only
     */
    @PutMapping("/plans/settings")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseData<PlansSettingsDTO>> updateSettings(
            @Valid @RequestBody PlansSettingsDTO settingsDTO) {
        try {
            PlansSettingsDTO settings = plansSettingsService.updateSettings(settingsDTO);
            return ResponseEntity.ok(ResponseData.ok(settings));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ResponseData.error("Failed to update settings: " + e.getMessage()));
        }
    }
}
