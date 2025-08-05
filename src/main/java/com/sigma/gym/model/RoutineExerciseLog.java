package com.sigma.gym.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseLog {
    private Long id;
   private Long routineLogId;
    private Long exerciseId;
    private Long routineExerciseId;
    private Integer repsPerformed;
    private Integer setsPerformed;
    private Double weightUsed; // Cambiado a Double para mayor flexibilidad
    private Integer duration; // segundos o minutos, según definas
    private String notes;
    private Boolean completed;
    private String exerciseName; // útil para mostrar directo en el front
    private Boolean isWarmup; // Indica si es un ejercicio de calentamiento
    private Integer setsCompleted;
    private Integer repsCompleted;
}
