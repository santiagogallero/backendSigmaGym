package com.sigma.gym.services;

import com.sigma.gym.DTOs.FiscalProfileDTO;
import com.sigma.gym.DTOs.UpsertFiscalProfileRequest;
import com.sigma.gym.entity.FiscalProfileEntity;
import com.sigma.gym.exceptions.InvoicingException;
import com.sigma.gym.exceptions.UserNotFoundException;
import com.sigma.gym.repository.FiscalProfileRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiscalProfileService {

    private final FiscalProfileRepository fiscalProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public FiscalProfileDTO upsertProfile(Long userId, UpsertFiscalProfileRequest request) {
        log.info("Upserting fiscal profile for user: {}", userId);

        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        // Validate document uniqueness (except for current user)
        Optional<FiscalProfileEntity> existingByDocument = fiscalProfileRepository
            .findByDocumentTypeAndNumber(request.getDocumentType(), request.getDocumentNumber());
        
        if (existingByDocument.isPresent() && !existingByDocument.get().getUserId().equals(userId)) {
            throw new InvoicingException.ProfileInvalidException(
                "Document already registered for another user: " + request.getDocumentType() + " " + request.getDocumentNumber());
        }

        // Validate CUIT format if provided
        if (request.getTaxId() != null && !request.getTaxId().isEmpty()) {
            if (!isValidCuitFormat(request.getTaxId())) {
                throw new InvoicingException.ProfileInvalidException("Invalid CUIT format: " + request.getTaxId());
            }
            
            // Check CUIT uniqueness (except for current user)
            Optional<FiscalProfileEntity> existingByCuit = fiscalProfileRepository.findByTaxId(request.getTaxId());
            if (existingByCuit.isPresent() && !existingByCuit.get().getUserId().equals(userId)) {
                throw new InvoicingException.ProfileInvalidException("CUIT already registered for another user: " + request.getTaxId());
            }
        }

        // Find existing or create new profile
        FiscalProfileEntity profile = fiscalProfileRepository.findByUserId(userId)
            .orElse(FiscalProfileEntity.builder()
                .userId(userId)
                .build());

        // Update profile with request data
        updateProfileFromRequest(profile, request);

        // Save and return
        FiscalProfileEntity savedProfile = fiscalProfileRepository.save(profile);
        log.info("Fiscal profile upserted successfully for user: {} with ID: {}", userId, savedProfile.getId());

        return mapToDTO(savedProfile);
    }

    @Transactional(readOnly = true)
    public Optional<FiscalProfileDTO> getMyProfile(Long userId) {
        log.debug("Getting fiscal profile for user: {}", userId);
        
        return fiscalProfileRepository.findByUserId(userId)
            .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public FiscalProfileEntity getEntityByUserId(Long userId) {
        return fiscalProfileRepository.findByUserId(userId)
            .orElse(null);
    }

    private void updateProfileFromRequest(FiscalProfileEntity profile, UpsertFiscalProfileRequest request) {
        profile.setLegalName(request.getLegalName());
        profile.setTaxId(request.getTaxId());
        profile.setDocumentType(request.getDocumentType());
        profile.setDocumentNumber(request.getDocumentNumber());
        profile.setAddressLine(request.getAddressLine());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setPostalCode(request.getPostalCode());
        profile.setCountry(request.getCountry());
    }

    private FiscalProfileDTO mapToDTO(FiscalProfileEntity entity) {
        return FiscalProfileDTO.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .legalName(entity.getLegalName())
            .taxId(entity.getTaxId())
            .documentType(entity.getDocumentType())
            .documentNumber(entity.getDocumentNumber())
            .addressLine(entity.getAddressLine())
            .city(entity.getCity())
            .state(entity.getState())
            .postalCode(entity.getPostalCode())
            .country(entity.getCountry())
            .build();
    }

    private boolean isValidCuitFormat(String cuit) {
        if (cuit == null || cuit.isEmpty()) {
            return false;
        }
        
        // CUIT format: XX-XXXXXXXX-X (11 digits with hyphens)
        String cleanCuit = cuit.replaceAll("-", "");
        if (cleanCuit.length() != 11) {
            return false;
        }
        
        // Check if all characters are digits
        return cleanCuit.matches("\\d{11}");
    }
}
