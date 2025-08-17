package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sigma.gym.model.WorkoutPlanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "slug"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, referencedColumnName = "id")
    @JsonIgnore
    private UserEntity owner;

    @Column(nullable = false, length = 100)
    private String name; // Display name (can be non-unique)

    @Column(nullable = false, length = 120, unique = false) // Unique constraint handled by DB composite key
    private String slug; // URL-friendly unique identifier per owner

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WorkoutPlanStatus status = WorkoutPlanStatus.DRAFT;

    @Column(length = 500)
    private String goal;

    @Column(length = 1000)
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 50)
    private String difficulty;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    private Long version; // For optimistic locking

    // Relación con el entrenador (UserEntity) - DEPRECATED, use owner instead
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", referencedColumnName = "id")
    @JsonIgnore // Evitar ciclos de serialización
    private UserEntity trainer;

    // Relación con días del plan
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("workoutplan-days")
    @Builder.Default
    private List<PlanDayEntity> planDays = new ArrayList<>();

    // Relación con rutinas - DEPRECATED, use planDays instead
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("workoutplan-routines")
    @Builder.Default
    private List<RoutineEntity> routines = new ArrayList<>();

    // Relación Many-to-Many con miembros
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "workout_plan_members",
        joinColumns = @JoinColumn(name = "workout_plan_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore // Evitar ciclos de serialización
    @Builder.Default
    private List<UserEntity> members = new ArrayList<>();

    // Helper methods
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public int getTotalDays() {
        return planDays != null ? planDays.size() : 0;
    }

    public int getTotalRoutines() {
        return routines != null ? routines.size() : 0;
    }

    public boolean hasMembers() {
        return members != null && !members.isEmpty();
    }

    public boolean isDraft() {
        return WorkoutPlanStatus.DRAFT.equals(status);
    }

    public boolean isActive() {
        return WorkoutPlanStatus.ACTIVE.equals(status);
    }

    public boolean isArchived() {
        return WorkoutPlanStatus.ARCHIVED.equals(status);
    }

    // Update timestamp on any modification
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
