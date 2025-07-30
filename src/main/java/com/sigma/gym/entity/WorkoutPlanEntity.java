package com.sigma.gym.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String goal;
    private String difficulty;
    private String notes;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "trainer_id") // Quién lo creó
    private UserEntity trainer;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL)
    private List<RoutineEntity> routines;

  @ManyToMany(mappedBy = "assignedPlans")  // 👈 este es el fix
    private List<UserEntity> members;
}
