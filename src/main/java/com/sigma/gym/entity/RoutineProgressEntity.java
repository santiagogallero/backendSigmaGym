package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "routine_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con UserEntity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id")
    private ProgressEntity progress;

    // Relación con RoutineEntity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", nullable = false)
    private RoutineEntity routine;

    @Column(name = "completed_days", nullable = false)
    private int completedDays;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @Column(name = "is_completed", nullable = false)
    @Getter
    @Setter
    private boolean isCompleted;

}
