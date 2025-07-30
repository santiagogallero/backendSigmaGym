package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String category; // Ej: Cardio, Strength, etc.

    private String equipment; // Ej: Dumbbell, Barbell, etc.

    private Integer duration; // en segundos (opcional, útil si es cardio o isométrico)

    private Integer sets;

    private Integer reps;

    private String videoUrl;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
    // ⚠️ Si querés saber en qué rutinas se usa este ejercicio:
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private java.util.List<RoutineExerciseEntity> routineExercises;
}
