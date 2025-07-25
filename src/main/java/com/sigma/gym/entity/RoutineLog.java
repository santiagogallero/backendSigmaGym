package com.sigma.gym.entity;
import java.time.LocalDateTime;

import java.util.List;

import jakarta.persistence.CascadeType;
import org.springframework.data.annotation.Id;
import java.util.ArrayList;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkOutLog workoutLog;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;

    @Builder.Default
    @OneToMany(mappedBy = "routineLog", cascade = CascadeType.ALL)
    private List<RoutineExerciseLog> exerciseLogs = new ArrayList<>();
}
