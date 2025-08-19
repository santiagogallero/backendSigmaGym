package com.sigma.gym.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigma.gym.entity.WaitlistEntryEntity;
import com.sigma.gym.entity.WaitlistEntryEntity.WaitlistStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistEntryDTO {
    private Long id;
    private Long classSessionId;
    private Long userId;
    private Integer position;
    private WaitlistStatus status;
    private String note;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime promotedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime holdUntil;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Class session details
    private String className;
    private String classType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime classStartsAt;
    
    private String trainerName;
    
    // Helper properties
    private boolean canConfirm;
    private boolean isHoldExpired;
    private long minutesRemainingToConfirm;

    public static WaitlistEntryDTO fromEntity(WaitlistEntryEntity entry) {
        if (entry == null) {
            return null;
        }

        WaitlistEntryDTOBuilder builder = WaitlistEntryDTO.builder()
            .id(entry.getId())
            .classSessionId(entry.getClassSessionId())
            .userId(entry.getUserId())
            .position(entry.getPosition())
            .status(entry.getStatus())
            .note(entry.getNote())
            .promotedAt(entry.getPromotedAt())
            .holdUntil(entry.getHoldUntil())
            .createdAt(entry.getCreatedAt())
            .canConfirm(entry.canConfirm())
            .isHoldExpired(entry.isHoldExpired());

        // Add class session details if available
        if (entry.getClassSession() != null) {
            builder
                .className(entry.getClassSession().getClassName())
                .classType(entry.getClassSession().getClassType())
                .classStartsAt(entry.getClassSession().getStartsAt());
            
            // Add trainer name if available
            if (entry.getClassSession().getTrainer() != null) {
                builder.trainerName(
                    entry.getClassSession().getTrainer().getFirstName() + " " +
                    entry.getClassSession().getTrainer().getLastName()
                );
            }
        }

        // Calculate minutes remaining to confirm
        if (entry.getHoldUntil() != null && entry.canConfirm()) {
            long minutesRemaining = java.time.Duration.between(
                LocalDateTime.now(), entry.getHoldUntil()).toMinutes();
            builder.minutesRemainingToConfirm(Math.max(0, minutesRemaining));
        }

        return builder.build();
    }
}
