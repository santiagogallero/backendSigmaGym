package com.sigma.gym.DTOs;

import com.sigma.gym.entity.AssignedExerciseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedExerciseResponseDTO {
    
    private Long id;
    private ExerciseLibraryResponseDTO exerciseLibrary;
    private UserSummaryDTO assignedToUser;
    private UserSummaryDTO assignedByTrainer;
    private LocalDate assignedDate;
    private LocalDate dueDate;
    private AssignedExerciseEntity.AssignmentStatus status;
    private Integer customSets;
    private Integer customReps;
    private Integer customDurationMinutes;
    private String customInstructions;
    private String trainerNotes;
    private String memberNotes;
    private AssignedExerciseEntity.Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Boolean isOverdue;
    private Integer daysUntilDue;
    private Integer completionCount; // Número de veces completado este ejercicio
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
