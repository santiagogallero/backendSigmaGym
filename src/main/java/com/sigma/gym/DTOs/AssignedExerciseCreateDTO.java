package com.sigma.gym.DTOs;

import com.sigma.gym.entity.AssignedExerciseEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedExerciseCreateDTO {
    
    @NotNull(message = "El ID del ejercicio de biblioteca es obligatorio")
    private Long exerciseLibraryId;
    
    @NotNull(message = "El ID del usuario asignado es obligatorio")
    private Long assignedToUserId;
    
    private LocalDate dueDate;
    
    private Integer customSets;
    
    private Integer customReps;
    
    private Integer customDurationMinutes;
    
    @Size(max = 500, message = "Las instrucciones personalizadas no pueden exceder 500 caracteres")
    private String customInstructions;
    
    @Size(max = 1000, message = "Las notas del entrenador no pueden exceder 1000 caracteres")
    private String trainerNotes;
    
    @Builder.Default
    private AssignedExerciseEntity.Priority priority = AssignedExerciseEntity.Priority.MEDIUM;
}
