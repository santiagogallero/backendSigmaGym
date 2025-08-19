package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "enhanced_routine_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedRoutineProgressEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private RoutineEntity routine;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column
    private LocalDate targetEndDate;
    
    @Column
    private LocalDate actualEndDate;
    
    @Column(nullable = false)
    private Integer totalDaysPlanned;
    
    @Column(nullable = false)
    private Integer completedDays = 0;
    
    @Column(nullable = false)
    private Integer skippedDays = 0;
    
    // Tracking de fechas de completitud
    @ElementCollection
    @CollectionTable(
        name = "routine_completion_dates",
        joinColumns = @JoinColumn(name = "routine_progress_id")
    )
    @Column(name = "completion_date")
    private Set<LocalDate> completionDates;
    
    // Métricas de progreso
    @Column
    private Double completionPercentage = 0.0;
    
    @Column
    private Double averageSessionDuration; // en minutos
    
    @Column
    private Double totalTimeSpent = 0.0; // en horas
    
    // Estado del progreso
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProgressStatus status = ProgressStatus.ACTIVE;
    
    // Dificultad percibida (1-10)
    @Column
    private Integer perceivedDifficulty;
    
    // Satisfacción (1-10)
    @Column
    private Integer satisfactionRating;
    
    @Column(length = 1000)
    private String userNotes;
    
    @Column(length = 1000)
    private String trainerNotes;
    
    // Fecha de la última sesión completada
    @Column
    private LocalDateTime lastSessionDate;
    
    // Siguiente sesión programada
    @Column
    private LocalDateTime nextScheduledSession;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
        calculateCompletionPercentage();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateCompletionPercentage();
        updateStatus();
    }
    
    private void calculateCompletionPercentage() {
        if (totalDaysPlanned != null && totalDaysPlanned > 0) {
            this.completionPercentage = (double) completedDays / totalDaysPlanned * 100;
            this.completionPercentage = Math.round(this.completionPercentage * 100.0) / 100.0;
        }
    }
    
    private void updateStatus() {
        if (completionPercentage >= 100.0) {
            this.status = ProgressStatus.COMPLETED;
            if (this.actualEndDate == null) {
                this.actualEndDate = LocalDate.now();
            }
        } else if (targetEndDate != null && LocalDate.now().isAfter(targetEndDate)) {
            this.status = ProgressStatus.OVERDUE;
        }
    }
    
    // Método auxiliar para agregar una fecha de completitud
    public void addCompletionDate(LocalDate date) {
        if (this.completionDates != null) {
            this.completionDates.add(date);
            this.completedDays = this.completionDates.size();
        }
    }
    
    public enum ProgressStatus {
        ACTIVE,
        PAUSED,
        COMPLETED,
        CANCELLED,
        OVERDUE,
        ON_HOLD
    }
}
