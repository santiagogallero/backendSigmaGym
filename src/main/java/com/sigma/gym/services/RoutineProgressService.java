package com.sigma.gym.services;

import com.sigma.gym.model.RoutineProgress;

import java.util.List;

public interface RoutineProgressService {
    RoutineProgress createRoutineProgress(RoutineProgress progress);
    RoutineProgress getRoutineProgressById(Long id);
    List<RoutineProgress> getRoutineProgressByUser(Long userId);
    List<RoutineProgress> getRoutineProgressByRoutine(Long routineId);
    RoutineProgress updateRoutineProgress(Long id, RoutineProgress progress);
    void deleteRoutineProgress(Long id);
}
