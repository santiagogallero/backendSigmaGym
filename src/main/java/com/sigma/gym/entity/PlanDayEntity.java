package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_days",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"plan_id", "order_index"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"workoutPlan", "exercises"})
@ToString(exclude = {"workoutPlan", "exercises"})
public class PlanDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonBackReference("workoutplan-days")
    private WorkoutPlanEntity workoutPlan;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "planDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("planday-exercises")
    @Builder.Default
    private List<PlanExerciseEntity> exercises = new ArrayList<>();

    // Helper methods
    public Long getPlanId() {
        return workoutPlan != null ? workoutPlan.getId() : null;
    }

    public int getTotalExercises() {
        return exercises != null ? exercises.size() : 0;
    }
}
