package com.sigma.gym.DTOs;

import com.sigma.gym.entity.EnhancedRoutineProgressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedRoutineProgressResponseDTO {
    
    private Long id;
    private RoutineSummaryDTO routine;
    private UserSummaryDTO user;
    private LocalDate startDate;
    private LocalDate targetEndDate;
    private LocalDate actualEndDate;
    private Integer totalDaysPlanned;
    private Integer completedDays;
    private Integer skippedDays;
    private Set<LocalDate> completionDates;
    private Double completionPercentage;
    private Double averageSessionDuration;
    private Double totalTimeSpent;
    private EnhancedRoutineProgressEntity.ProgressStatus status;
    private Integer perceivedDifficulty;
    private Integer satisfactionRating;
    private String userNotes;
    private String trainerNotes;
    private LocalDateTime lastSessionDate;
    private LocalDateTime nextScheduledSession;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isOnTrack;
    private Integer daysRemaining;
    private Double expectedCompletionRate;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutineSummaryDTO {
        private Long id;
        private String name;
        private String description;
        private String difficulty;
        private Integer duration;
        private String dayOfWeek;
    }
    
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
