package com.sigma.gym.controllers.api;

import com.sigma.gym.DTOs.plans.PlanDTO;
import com.sigma.gym.DTOs.plans.PlansSettingsDTO;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.PlanService;
import com.sigma.gym.services.PlansSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PublicPlansController {
    
    private final PlanService planService;
    private final PlansSettingsService plansSettingsService;
    
    /**
     * Get all visible plans ordered by sortOrder
     */
    @GetMapping("/plans")
    public ResponseEntity<ResponseData<List<PlanDTO>>> getVisiblePlans() {
        try {
            List<PlanDTO> plans = planService.getVisiblePlans();
            return ResponseEntity.ok(ResponseData.ok(plans));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ResponseData.error("Failed to retrieve plans"));
        }
    }
    
    /**
     * Get public settings
     */
    @GetMapping("/plans/settings")
    public ResponseEntity<ResponseData<PlansSettingsDTO>> getPublicSettings() {
        try {
            PlansSettingsDTO settings = plansSettingsService.getSettings();
            return ResponseEntity.ok(ResponseData.ok(settings));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ResponseData.error("Failed to retrieve settings"));
        }
    }
}
