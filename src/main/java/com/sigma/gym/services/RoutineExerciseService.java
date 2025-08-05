package com.sigma.gym.services;

import com.sigma.gym.model.RoutineExercise;
import java.util.List;

public interface RoutineExerciseService {
    RoutineExercise addRoutineExercise(RoutineExercise routineExercise);
    List<RoutineExercise> getExercisesByRoutine(Long routineId);
    RoutineExercise updateRoutineExercise(Long id, RoutineExercise routineExercise);
    void deleteRoutineExercise(Long id);
}
