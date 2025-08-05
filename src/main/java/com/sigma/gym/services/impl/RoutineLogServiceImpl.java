package com.sigma.gym.services.impl;

import com.sigma.gym.entity.RoutineEntity;
import com.sigma.gym.entity.RoutineLogEntity;
import com.sigma.gym.entity.WorkOutLogEntity;

import com.sigma.gym.mappers.RoutineLogMapper;
import com.sigma.gym.model.RoutineLog;
import com.sigma.gym.repository.RoutineLogRepository;
import com.sigma.gym.repository.RoutineRepository;
import com.sigma.gym.repository.WorkoutLogRepository;
import com.sigma.gym.services.RoutineLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineLogServiceImpl implements RoutineLogService {

    private final RoutineLogRepository routineLogRepository;
    private final RoutineRepository routineRepository;
    private final WorkoutLogRepository workoutLogRepository;

    public RoutineLogServiceImpl(
            RoutineLogRepository routineLogRepository,
            RoutineRepository routineRepository,
            WorkoutLogRepository workoutLogRepository
    ) {
        this.routineLogRepository = routineLogRepository;
        this.routineRepository = routineRepository;
        this.workoutLogRepository = workoutLogRepository;
    }

    @Override
    public RoutineLog createRoutineLog(RoutineLog log) {
        RoutineLogEntity entity = RoutineLogMapper.toEntity(log);

        // Asignar relaciones
        if (log.getRoutineId() != null) {
            RoutineEntity routine = routineRepository.findById(log.getRoutineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));
            entity.setRoutine(routine);
        }

        if (log.getWorkoutLogId() != null) {
            WorkOutLogEntity workoutLog = workoutLogRepository.findById(log.getWorkoutLogId())
                    .orElseThrow(() -> new ResourceNotFoundException("WorkoutLog not found"));
            entity.setWorkoutLog(workoutLog);
        }

        return RoutineLogMapper.toDomain(routineLogRepository.save(entity));
    }

    @Override
    public RoutineLog getRoutineLogById(Long id) {
        RoutineLogEntity entity = routineLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineLog not found with id: " + id));
        return RoutineLogMapper.toDomain(entity);
    }

    @Override
    public List<RoutineLog> getAllRoutineLogs() {
        return routineLogRepository.findAll()
                .stream()
                .map(RoutineLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public RoutineLog updateRoutineLog(Long id, RoutineLog log) {
        RoutineLogEntity entity = routineLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineLog not found with id: " + id));

        entity.setStartTime(log.getStartTime());
        entity.setEndTime(log.getEndTime());
        entity.setNotes(log.getNotes());
        entity.setCompleted(log.getCompleted());

        if (log.getRoutineId() != null) {
            RoutineEntity routine = routineRepository.findById(log.getRoutineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));
            entity.setRoutine(routine);
        }

        if (log.getWorkoutLogId() != null) {
            WorkOutLogEntity workoutLog = workoutLogRepository.findById(log.getWorkoutLogId())
                    .orElseThrow(() -> new ResourceNotFoundException("WorkoutLog not found"));
            entity.setWorkoutLog(workoutLog);
        }

        return RoutineLogMapper.toDomain(routineLogRepository.save(entity));
    }

    @Override
    public void deleteRoutineLog(Long id) {
        RoutineLogEntity entity = routineLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineLog not found with id: " + id));
        routineLogRepository.delete(entity);
    }
}
