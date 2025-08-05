package com.sigma.gym.services;

import com.sigma.gym.model.WorkoutLog;
import java.util.List;

public interface WorkoutLogService {
    WorkoutLog createWorkoutLog(WorkoutLog log);
    WorkoutLog getWorkoutLogById(Long id);
    List<WorkoutLog> getAllWorkoutLogs();
    WorkoutLog updateWorkoutLog(Long id, WorkoutLog log);
    void deleteWorkoutLog(Long id);
}
