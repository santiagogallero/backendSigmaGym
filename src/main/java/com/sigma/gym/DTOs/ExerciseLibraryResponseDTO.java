package com.sigma.gym.DTOs;

import com.sigma.gym.entity.ExerciseLibraryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLibraryResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private ExerciseLibraryEntity.ExerciseCategory category;
    private ExerciseLibraryEntity.DifficultyLevel difficultyLevel;
    private String targetMuscles;
    private String equipment;
    private String instructions;
    private String videoUrl;
    private String imageUrl;
    private Integer estimatedDurationMinutes;
    private Integer suggestedSets;
    private Integer suggestedReps;
    private Boolean isActive;
    private String createdByTrainerName;
    private Long createdByTrainerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalAssignments; // Número total de asignaciones
}
