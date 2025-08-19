package com.sigma.gym.DTOs;

import com.sigma.gym.entity.ExerciseSessionLogEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSessionLogCreateDTO {
    
    private Long assignedExerciseId;
    
    private Long routineProgressId;
    
    private LocalDate sessionDate;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Min(value = 0, message = "Las series completadas no pueden ser negativas")
    private Integer setsCompleted;
    
    @Min(value = 0, message = "Las repeticiones completadas no pueden ser negativas")
    private Integer repsCompleted;
    
    @Min(value = 0, message = "El peso usado no puede ser negativo")
    private Double weightUsed;
    
    @Size(max = 10, message = "La unidad de peso no puede exceder 10 caracteres")
    private String weightUnit = "kg";
    
    @Min(value = 0, message = "La distancia no puede ser negativa")
    private Double distanceCovered;
    
    @Size(max = 10, message = "La unidad de distancia no puede exceder 10 caracteres")
    private String distanceUnit = "km";
    
    @Min(value = 0, message = "Las calorías no pueden ser negativas")
    private Integer caloriesBurned;
    
    @Min(value = 0, message = "La frecuencia cardíaca no puede ser negativa")
    private Integer averageHeartRate;
    
    @Min(value = 0, message = "La frecuencia cardíaca máxima no puede ser negativa")
    private Integer maxHeartRate;
    
    @Min(value = 1, message = "El RPE debe estar entre 1 y 10")
    @Max(value = 10, message = "El RPE debe estar entre 1 y 10")
    private Integer rpeRating;
    
    @Min(value = 1, message = "La satisfacción debe estar entre 1 y 10")
    @Max(value = 10, message = "La satisfacción debe estar entre 1 y 10")
    private Integer satisfactionRating;
    
    @Builder.Default
    private ExerciseSessionLogEntity.SessionStatus status = ExerciseSessionLogEntity.SessionStatus.COMPLETED;
    
    @Size(max = 1000, message = "Las notas del usuario no pueden exceder 1000 caracteres")
    private String userNotes;
    
    @Min(value = 1, message = "El nivel de energía debe estar entre 1 y 10")
    @Max(value = 10, message = "El nivel de energía debe estar entre 1 y 10")
    private Integer energyLevelBefore;
    
    @Min(value = 1, message = "El nivel de energía debe estar entre 1 y 10")
    @Max(value = 10, message = "El nivel de energía debe estar entre 1 y 10")
    private Integer energyLevelAfter;
    
    @Size(max = 200, message = "Las condiciones climáticas no pueden exceder 200 caracteres")
    private String weatherConditions;
    
    @Size(max = 200, message = "El equipamiento usado no puede exceder 200 caracteres")
    private String equipmentUsed;
}
