package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routine_id")  // ✅ Especifica explícitamente el nombre
    private RoutineEntity routine;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEntity exercise;

    private Integer sets;
    private Integer reps;
    private Integer weight; // peso sugerido
    private String exerciseName;
    private Boolean isWarmup; // true si es parte de calentamiento

    // ✅ AGREGA MÉTODOS HELPER para obtener los IDs:
    public Long getRoutineId() {
        return routine != null ? routine.getId() : null;
    }

    public Long getExerciseId() {
        return exercise != null ? exercise.getId() : null;
    }
}
