package com.sigma.gym.services.impl;

import com.sigma.gym.DTOs.plans.PlansSettingsDTO;
import com.sigma.gym.entity.PlansSettingsEntity;
import com.sigma.gym.mappers.PlanMapper;
import com.sigma.gym.repository.PlansSettingsRepository;
import com.sigma.gym.services.PlansSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlansSettingsServiceImpl implements PlansSettingsService {
    
    private final PlansSettingsRepository settingsRepository;
    private final PlanMapper planMapper;
    
    @Override
    public PlansSettingsDTO getSettings() {
        PlansSettingsEntity entity = settingsRepository.getSettings()
                .orElse(createDefaultSettings());
        return planMapper.toSettingsDTO(entity);
    }
    
    @Override
    @Transactional
    public PlansSettingsDTO updateSettings(PlansSettingsDTO settingsDTO) {
        PlansSettingsEntity entity = planMapper.toSettingsEntity(settingsDTO);
        PlansSettingsEntity savedEntity = settingsRepository.save(entity);
        return planMapper.toSettingsDTO(savedEntity);
    }
    
    private PlansSettingsEntity createDefaultSettings() {
        return PlansSettingsEntity.builder()
                .id(1L)
                .paymentShowOnPage(true)
                .build();
    }
}
