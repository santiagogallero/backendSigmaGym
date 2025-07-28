package com.sigma.gym.services;

import com.sigma.gym.entity.Routine;

import java.util.List;

public interface RoutineService {
    Routine saveRoutine(Routine routine);
    List<Routine> getRoutinesByUserId(Long userId);
    Routine getRoutineById(Long id);
    void deleteRoutine(Long id);
}
