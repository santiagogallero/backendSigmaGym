package com.sigma.gym.services.impl;

import com.sigma.gym.entity.RoutineExerciseEntity;
import com.sigma.gym.mappers.RoutineExerciseMapper;
import com.sigma.gym.model.RoutineExercise;
import com.sigma.gym.repository.RoutineExerciseRepository;
import com.sigma.gym.services.RoutineExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sigma.gym.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineExerciseServiceImpl implements RoutineExerciseService {

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    @Override
    public RoutineExercise addRoutineExercise(RoutineExercise routineExercise) {
        RoutineExerciseEntity entity = RoutineExerciseMapper.toEntity(routineExercise);
        return RoutineExerciseMapper.toModel(routineExerciseRepository.save(entity));
    }

    @Override
    public List<RoutineExercise> getExercisesByRoutine(Long routineId) {
    return routineExerciseRepository.findByRoutine_Id(routineId) // ✅ cambio aquí
            .stream()
            .map(RoutineExerciseMapper::toModel)
            .collect(Collectors.toList());
}

@Override

public RoutineExercise updateRoutineExercise(Long id, RoutineExercise routineExercise) {
    RoutineExerciseEntity entity = findEntityById(id);

    entity.setSets(routineExercise.getSets());
    entity.setReps(routineExercise.getReps());
    entity.setWeight(routineExercise.getWeight());
    entity.setIsWarmup(routineExercise.getIsWarmup());
    entity.setExerciseName(routineExercise.getExerciseName());

    return RoutineExerciseMapper.toModel(routineExerciseRepository.save(entity));
}

@Override

public void deleteRoutineExercise(Long id) {
    RoutineExerciseEntity entity = findEntityById(id);
    routineExerciseRepository.delete(entity);
}

/**
 * Método auxiliar para buscar una RoutineExerciseEntity por ID
 * Lanza ResourceNotFoundException si no existe.
 */
private RoutineExerciseEntity findEntityById(Long id) {
    return routineExerciseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RoutineExercise not found with id: " + id));
}

}
