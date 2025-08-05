package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineExerciseLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el log de rutina
    @ManyToOne
    @JoinColumn(name = "routine_log_id", nullable = false)
    private RoutineLogEntity routineLog;

    // ✅ Relación directa con WorkoutLog (para mappedBy en WorkOutLogEntity)
    @ManyToOne
    @JoinColumn(name = "workout_log_id")
    private WorkOutLogEntity workoutLog;

    // Relación con el ejercicio
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEntity exercise;

    // Relación con la configuración original de la rutina
    @ManyToOne
    @JoinColumn(name = "routine_exercise_id")
    private RoutineExerciseEntity routineExercise;

    private Integer repsPerformed;
    private Integer setsPerformed;

    private Double weightUsed;

    private Integer duration; // segundos o minutos
    private String notes;
    private Boolean completed;

    private String exerciseName; // útil para mostrar en front
    private Boolean isWarmup; // indica si es calentamiento
    private Integer setsCompleted;
    private Integer repsCompleted;
}
