package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plan_exercises",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"day_id", "order_index"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"planDay"})
@ToString(exclude = {"planDay"})
public class PlanExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    @JsonBackReference("planday-exercises")
    private PlanDayEntity planDay;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column
    private Integer reps;

    @Column
    private Integer sets;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(length = 10)
    private String weightUnit;

    @Column(name = "rest_time_seconds")
    private Integer restTimeSeconds;

    @Column(name = "is_warmup", nullable = false)
    @Builder.Default
    private Boolean isWarmup = false;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(length = 1000)
    private String notes;

    // Helper methods
    public Long getDayId() {
        return planDay != null ? planDay.getId() : null;
    }

    public Long getPlanId() {
        return planDay != null && planDay.getWorkoutPlan() != null ? 
               planDay.getWorkoutPlan().getId() : null;
    }
}
