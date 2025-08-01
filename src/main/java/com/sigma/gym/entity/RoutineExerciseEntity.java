package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routine_exercises")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Claves foráneas con nombres claros
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference("routine-exercises")
    private RoutineEntity routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
    private ExerciseEntity exercise;

    private Integer sets;
    private Integer reps;
    private Integer weight;

    @Column(name = "exercise_name", length = 100)
    private String exerciseName;

    @Column(name = "is_warmup")
    @Builder.Default
    private Boolean isWarmup = false;

    // ✅ Métodos helper que respetan SRP
    public Long getRoutineId() {
        return routine != null ? routine.getId() : null;
    }

    public Long getExerciseId() {
        return exercise != null ? exercise.getId() : null;
    }

    // ✅ Método de negocio simple
    public boolean isValidConfiguration() {
        return sets != null && sets > 0 && reps != null && reps > 0;
    }
}
