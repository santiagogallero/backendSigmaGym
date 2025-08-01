package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Integer duration;
    private String difficulty;
    private String dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlanEntity workoutPlan;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("routine-exercises") // ✅ Controla serialización
    private List<RoutineExerciseEntity> routineExercises;
}
