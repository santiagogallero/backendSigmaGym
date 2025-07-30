package com.sigma.gym.services.impl;

import com.sigma.gym.entity.RoutineEntity;
import com.sigma.gym.repository.RoutineRepository;
import com.sigma.gym.services.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutineServiceImpl implements RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    @Override
    public RoutineEntity saveRoutine(RoutineEntity routine) {
        return routineRepository.save(routine);
    }

    @Override
    public List<RoutineEntity> getRoutinesByTrainerId(Long trainerId) {
        return routineRepository.findByTrainerId(trainerId);
    }

    @Override
    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
    }

    @Override
    public void deleteRoutine(Long id) {
        routineRepository.deleteById(id);
    }
}
