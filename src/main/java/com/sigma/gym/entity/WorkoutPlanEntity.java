package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

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
    private LocalDate createdAt;

    // ✅ Relación con clave foránea clara
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", referencedColumnName = "id")
    private UserEntity trainer;

    // ✅ Evitar referencia circular con @JsonManagedReference
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("workoutplan-routines")
    @Builder.Default
    private List<RoutineEntity> routines = new ArrayList<>();

    // ✅ Relación Many-to-Many con tabla intermedia
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "workout_plan_members",
        joinColumns = @JoinColumn(name = "workout_plan_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore // ✅ Evitar serialización circular
    @Builder.Default
    private List<UserEntity> members = new ArrayList<>();

    // ✅ Método helper que respeta SRP
    public int getTotalRoutines() {
        return routines != null ? routines.size() : 0;
    }

    // ✅ Método para verificar si tiene miembros asignados
    public boolean hasMembers() {
        return members != null && !members.isEmpty();
    }
}
