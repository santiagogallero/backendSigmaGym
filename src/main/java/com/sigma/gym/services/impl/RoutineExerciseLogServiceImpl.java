package com.sigma.gym.services.impl;

import com.sigma.gym.entity.ExerciseEntity;
import com.sigma.gym.entity.RoutineExerciseEntity;
import com.sigma.gym.entity.RoutineExerciseLogEntity;
import com.sigma.gym.entity.RoutineLogEntity;
import com.sigma.gym.mappers.RoutineExerciseLogMapper;
import com.sigma.gym.model.RoutineExerciseLog;
import com.sigma.gym.repository.RoutineExerciseLogRepository;
import com.sigma.gym.services.RoutineExerciseLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sigma.gym.repository.*;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineExerciseLogServiceImpl implements RoutineExerciseLogService {

    @Autowired
    private RoutineExerciseLogRepository repository;

    @Autowired
    private RoutineLogRepository routineLogRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    @Override
    public RoutineExerciseLog createRoutineExerciseLog(RoutineExerciseLog log) {
        RoutineExerciseLogEntity entity = RoutineExerciseLogMapper.toEntity(log);
        return RoutineExerciseLogMapper.toDomain(repository.save(entity));
    }

    @Override
    public RoutineExerciseLog getRoutineExerciseLogById(Long id) {
        RoutineExerciseLogEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineExerciseLog not found with id: " + id));
        return RoutineExerciseLogMapper.toDomain(entity);
    }

    @Override
    public List<RoutineExerciseLog> getAllRoutineExerciseLogs() {
        return repository.findAll()
                .stream()
                .map(RoutineExerciseLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoutineExerciseLog> getLogsByWorkoutLogId(Long workoutLogId) {
        return repository.findByWorkoutLogId(workoutLogId)
                .stream()
                .map(RoutineExerciseLogMapper::toDomain)
                .collect(Collectors.toList());
    }

  @Override
public RoutineExerciseLog updateRoutineExerciseLog(Long id, RoutineExerciseLog log) {
    RoutineExerciseLogEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RoutineExerciseLog not found with id: " + id));

    // Actualizar relaciones
    if (log.getRoutineLogId() != null) {
        RoutineLogEntity routineLog = routineLogRepository.findById(log.getRoutineLogId())
                .orElseThrow(() -> new ResourceNotFoundException("RoutineLog not found"));
        entity.setRoutineLog(routineLog);
    }

    if (log.getExerciseId() != null) {
        ExerciseEntity exercise = exerciseRepository.findById(log.getExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
        entity.setExercise(exercise);
    }

    if (log.getRoutineExerciseId() != null) {
        RoutineExerciseEntity routineExercise = routineExerciseRepository.findById(log.getRoutineExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("RoutineExercise not found"));
        entity.setRoutineExercise(routineExercise);
    }

    // Actualizar atributos simples
    entity.setRepsPerformed(log.getRepsPerformed());
    entity.setSetsPerformed(log.getSetsPerformed());
    entity.setWeightUsed(Double.valueOf(log.getWeightUsed())); // Conversión si en model es Double
    entity.setDuration(log.getDuration());
    entity.setNotes(log.getNotes());
    entity.setCompleted(log.getCompleted());
    entity.setExerciseName(log.getExerciseName());
    entity.setIsWarmup(log.getIsWarmup());
    entity.setSetsCompleted(log.getSetsCompleted());
    entity.setRepsCompleted(log.getRepsCompleted());

    return RoutineExerciseLogMapper.toDomain(repository.save(entity));
}


    @Override
    public void deleteRoutineExerciseLog(Long id) {
        RoutineExerciseLogEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineExerciseLog not found with id: " + id));
        repository.delete(entity);
    }
}
