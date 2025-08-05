package com.sigma.gym.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con WorkoutLog
    @ManyToOne
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkOutLogEntity workoutLog;

    // Relación con la rutina
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private RoutineEntity routine;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;

    private Boolean completed; // ✅ estado de finalización

    // Opcional: guardar duración en DB en vez de calcularla
    private Long totalDuration; // en minutos o segundos

    // Relación con logs de ejercicios
    @Builder.Default
    @OneToMany(mappedBy = "routineLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineExerciseLogEntity> exerciseLogs = new ArrayList<>();

    // Opcional: auditoría
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
}
