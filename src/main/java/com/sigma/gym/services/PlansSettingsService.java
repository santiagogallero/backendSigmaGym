package com.sigma.gym.services;

import com.sigma.gym.DTOs.plans.PlansSettingsDTO;

public interface PlansSettingsService {
    
    PlansSettingsDTO getSettings();
    
    PlansSettingsDTO updateSettings(PlansSettingsDTO settingsDTO);
}
