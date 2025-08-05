package com.sigma.gym.services;

import com.sigma.gym.model.Exercise;
import java.util.List;

public interface ExerciseService {
    Exercise createExercise(Exercise exercise);
    Exercise getExerciseById(Long id);
    List<Exercise> getAllExercises();
    Exercise updateExercise(Long id, Exercise exercise);
    void deleteExercise(Long id);
}
