package com.sigma.gym.services.impl;

import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.WorkOutLogEntity;
import com.sigma.gym.mappers.WorkOutLogMapper;
import com.sigma.gym.model.WorkoutLog;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.repository.WorkoutLogRepository;
import com.sigma.gym.services.WorkoutLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutLogServiceImpl implements WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;

    public WorkoutLogServiceImpl(
            WorkoutLogRepository workoutLogRepository,
            UserRepository userRepository
    ) {
        this.workoutLogRepository = workoutLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WorkoutLog createWorkoutLog(WorkoutLog log) {
        WorkOutLogEntity entity = WorkOutLogMapper.toEntity(log);

        // Asignar usuario
        if (log.getUserId() != null) {
            UserEntity user = userRepository.findById(log.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            entity.setUser(user);
        }

        return WorkOutLogMapper.toDomain(workoutLogRepository.save(entity));
    }

    @Override
    public WorkoutLog getWorkoutLogById(Long id) {
        WorkOutLogEntity entity = workoutLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutLog not found with id: " + id));
        return WorkOutLogMapper.toDomain(entity);
    }

    @Override
    public List<WorkoutLog> getAllWorkoutLogs() {
        return workoutLogRepository.findAll()
                .stream()
                .map(WorkOutLogMapper::toDomain)
                .collect(Collectors.toList());
    }

   @Override
public WorkoutLog updateWorkoutLog(Long id, WorkoutLog log) {
    WorkOutLogEntity entity = workoutLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("WorkoutLog not found with id: " + id));

    // Actualiza la fecha
    if (log.getDate() != null) {
        entity.setDate(log.getDate().toLocalDate()); // conversión si el model usa LocalDateTime
    }

    entity.setNotes(log.getNotes());

    if (log.getUserId() != null) {
        UserEntity user = userRepository.findById(log.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        entity.setUser(user);
    }

    return WorkOutLogMapper.toDomain(workoutLogRepository.save(entity));
}

    @Override
    public void deleteWorkoutLog(Long id) {
        WorkOutLogEntity entity = workoutLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutLog not found with id: " + id));
        workoutLogRepository.delete(entity);
    }
}
