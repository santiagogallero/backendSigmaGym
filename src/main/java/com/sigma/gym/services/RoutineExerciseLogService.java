package com.sigma.gym.services;

import com.sigma.gym.model.RoutineExerciseLog;
import java.util.List;

public interface RoutineExerciseLogService {
    RoutineExerciseLog createRoutineExerciseLog(RoutineExerciseLog log);
    RoutineExerciseLog getRoutineExerciseLogById(Long id);
    List<RoutineExerciseLog> getAllRoutineExerciseLogs();
    List<RoutineExerciseLog> getLogsByWorkoutLogId(Long workoutLogId);
    RoutineExerciseLog updateRoutineExerciseLog(Long id, RoutineExerciseLog log);
    void deleteRoutineExerciseLog(Long id);
}
