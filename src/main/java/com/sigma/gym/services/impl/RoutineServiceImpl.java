package com.sigma.gym.services.impl;

import com.sigma.gym.entity.RoutineEntity;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.mappers.RoutineMapper;
import com.sigma.gym.model.Routine;
import com.sigma.gym.repository.RoutineRepository;
import com.sigma.gym.repository.WorkoutPlanRepository;
import com.sigma.gym.services.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineServiceImpl implements RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Override
    public Routine createRoutine(Routine routine) {
        RoutineEntity entity = RoutineMapper.toEntity(routine);

        // Mapear workoutPlanId a WorkoutPlanEntity
        WorkoutPlanEntity workoutPlan = workoutPlanRepository.findById(routine.getWorkoutPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WorkoutPlan not found with id: " + routine.getWorkoutPlanId()
                ));
        entity.setWorkoutPlan(workoutPlan);

        return RoutineMapper.toDomain(routineRepository.save(entity));
    }

    @Override
    public Routine getRoutineById(Long id) {
        RoutineEntity entity = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));
        return RoutineMapper.toDomain(entity);
    }

    @Override
    public List<Routine> getAllRoutines() {
        return routineRepository.findAll()
                .stream()
                .map(RoutineMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Routine updateRoutine(Long id, Routine routine) {
        RoutineEntity entity = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));

        entity.setName(routine.getName());
        entity.setDescription(routine.getDescription());
        entity.setDuration(routine.getDuration());
        entity.setDifficulty(routine.getDifficulty());
        entity.setDayOfWeek(routine.getDayOfWeek());

        // Actualizar referencia a WorkoutPlan si viene en el request
        if (routine.getWorkoutPlanId() != null) {
            WorkoutPlanEntity workoutPlan = workoutPlanRepository.findById(routine.getWorkoutPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "WorkoutPlan not found with id: " + routine.getWorkoutPlanId()
                    ));
            entity.setWorkoutPlan(workoutPlan);
        }

        return RoutineMapper.toDomain(routineRepository.save(entity));
    }

    @Override
    public void deleteRoutine(Long id) {
        RoutineEntity entity = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));
        routineRepository.delete(entity);
    }
}
