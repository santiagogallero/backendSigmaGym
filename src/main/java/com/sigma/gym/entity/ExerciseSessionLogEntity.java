package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "exercise_session_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSessionLogEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_exercise_id")
    private AssignedExerciseEntity assignedExercise;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_progress_id")
    private EnhancedRoutineProgressEntity routineProgress;
    
    @Column(nullable = false)
    private LocalDate sessionDate;
    
    @Column
    private LocalDateTime startTime;
    
    @Column
    private LocalDateTime endTime;
    
    @Column
    private Integer durationMinutes;
    
    // Datos de rendimiento
    @Column
    private Integer setsCompleted;
    
    @Column
    private Integer repsCompleted;
    
    @Column
    private Double weightUsed;
    
    @Column(length = 10)
    private String weightUnit = "kg";
    
    @Column
    private Double distanceCovered; // para ejercicios de cardio
    
    @Column(length = 10)
    private String distanceUnit = "km";
    
    @Column
    private Integer caloriesBurned;
    
    @Column
    private Integer averageHeartRate;
    
    @Column
    private Integer maxHeartRate;
    
    // Percepción del esfuerzo (RPE Scale 1-10)
    @Column
    private Integer rpeRating;
    
    // Satisfacción con la sesión (1-10)
    @Column
    private Integer satisfactionRating;
    
    // Estado de la sesión
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SessionStatus status = SessionStatus.COMPLETED;
    
    @Column(length = 1000)
    private String userNotes;
    
    @Column(length = 1000)
    private String trainerFeedback;
    
    // Condiciones ambientales y corporales
    @Column
    private Integer energyLevelBefore; // 1-10
    
    @Column
    private Integer energyLevelAfter; // 1-10
    
    @Column(length = 200)
    private String weatherConditions;
    
    @Column(length = 200)
    private String equipmentUsed;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.sessionDate == null) {
            this.sessionDate = LocalDate.now();
        }
        calculateDuration();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateDuration();
    }
    
    private void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }
    
    public enum SessionStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        SKIPPED,
        CANCELLED,
        PARTIAL
    }
}
