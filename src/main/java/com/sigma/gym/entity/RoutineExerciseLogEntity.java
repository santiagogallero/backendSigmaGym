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

    @ManyToOne
    @JoinColumn(name = "routine_log_id", nullable = false)
    private RoutineLogEntity routineLog;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEntity exercise;

    @ManyToOne
    @JoinColumn(name = "routine_exercise_id")
    private RoutineExerciseEntity routineExercise;

    private Integer repsPerformed;
    private Integer setsPerformed;

    private BigDecimal weightUsed;

    private Integer duration; // segundos o minutos, según definas

    private String notes;

    private Boolean completed;
    
    private String exerciseName; // útil para mostrar directo en el front
    private Boolean isWarmup; // Indica si es un ejercicio de calentamiento
    private Integer setsCompleted;
    private Integer repsCompleted;

}
