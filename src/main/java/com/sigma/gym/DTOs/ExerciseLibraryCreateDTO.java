package com.sigma.gym.DTOs;

import com.sigma.gym.entity.ExerciseLibraryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLibraryCreateDTO {
    
    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @NotNull(message = "La categoría es obligatoria")
    private ExerciseLibraryEntity.ExerciseCategory category;
    
    @NotNull(message = "El nivel de dificultad es obligatorio")
    private ExerciseLibraryEntity.DifficultyLevel difficultyLevel;
    
    @Size(max = 100, message = "Los músculos objetivo no pueden exceder 100 caracteres")
    private String targetMuscles;
    
    @Size(max = 100, message = "El equipamiento no puede exceder 100 caracteres")
    private String equipment;
    
    @Size(max = 500, message = "Las instrucciones no pueden exceder 500 caracteres")
    private String instructions;
    
    @Size(max = 500, message = "La URL del video no puede exceder 500 caracteres")
    private String videoUrl;
    
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imageUrl;
    
    private Integer estimatedDurationMinutes;
    
    private Integer suggestedSets;
    
    private Integer suggestedReps;
}
