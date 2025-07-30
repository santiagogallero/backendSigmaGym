package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer duration; // in minutes
    private String difficulty;
    private String dayOfWeek; // "Monday", "Wednesday", etc.

    @ManyToOne
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlanEntity workoutPlan;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL)
    private List<RoutineExerciseEntity> routineExercises;
}
