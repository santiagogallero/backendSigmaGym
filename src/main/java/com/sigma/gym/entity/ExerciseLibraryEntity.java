package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "exercise_library")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLibraryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ExerciseCategory category;
    
    @Enumerated(EnumType.STRING) 
    @Column(length = 50)
    private DifficultyLevel difficultyLevel;
    
    @Column(length = 100)
    private String targetMuscles;
    
    @Column(length = 100)
    private String equipment;
    
    @Column(length = 500)
    private String instructions;
    
    @Column(length = 500)
    private String videoUrl;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column
    private Integer estimatedDurationMinutes;
    
    @Column
    private Integer suggestedSets;
    
    @Column
    private Integer suggestedReps;
    
    @Column
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_trainer_id")
    private UserEntity createdByTrainer;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Relación con ejercicios asignados
    @OneToMany(mappedBy = "exerciseLibrary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssignedExerciseEntity> assignedExercises;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum ExerciseCategory {
        CARDIO,
        STRENGTH,
        FLEXIBILITY,
        BALANCE,
        FUNCTIONAL,
        SPORTS_SPECIFIC,
        REHABILITATION,
        WARM_UP,
        COOL_DOWN
    }
    
    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE, 
        ADVANCED,
        EXPERT
    }
}
