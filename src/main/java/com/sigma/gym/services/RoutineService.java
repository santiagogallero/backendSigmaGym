package com.sigma.gym.services;

import com.sigma.gym.model.Routine;
import java.util.List;

public interface RoutineService {
    Routine createRoutine(Routine routine);
    Routine getRoutineById(Long id);
    List<Routine> getAllRoutines();
    Routine updateRoutine(Long id, Routine routine);
    void deleteRoutine(Long id);
}
