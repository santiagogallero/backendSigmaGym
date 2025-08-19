package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "assigned_exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedExerciseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_library_id", nullable = false)
    private ExerciseLibraryEntity exerciseLibrary;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id", nullable = false)
    private UserEntity assignedToUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_trainer_id", nullable = false)
    private UserEntity assignedByTrainer;
    
    @Column(nullable = false)
    private LocalDate assignedDate;
    
    @Column
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
    
    // Parámetros personalizados para esta asignación
    @Column
    private Integer customSets;
    
    @Column
    private Integer customReps;
    
    @Column
    private Integer customDurationMinutes;
    
    @Column(length = 500)
    private String customInstructions;
    
    @Column(length = 1000)
    private String trainerNotes;
    
    @Column(length = 1000)
    private String memberNotes;
    
    // Prioridad de la asignación
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Priority priority = Priority.MEDIUM;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.assignedDate == null) {
            this.assignedDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == AssignmentStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public enum AssignmentStatus {
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        PAUSED,
        CANCELLED,
        OVERDUE
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
