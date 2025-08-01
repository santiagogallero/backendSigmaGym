package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routines")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    private Integer duration;

    @Column(length = 50)
    private String difficulty;

    @Column(name = "day_of_week", length = 20)
    private String dayOfWeek;

    // ✅ Clave foránea con nombre claro
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference("workoutplan-routines")
    private WorkoutPlanEntity workoutPlan;

    // ✅ Inicializar lista y evitar ciclos
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("routine-exercises")
    @Builder.Default
    private List<RoutineExerciseEntity> routineExercises = new ArrayList<>();

    // ✅ Método helper que respeta SRP
    public int getTotalExercises() {
        return routineExercises != null ? routineExercises.size() : 0;
    }
}
