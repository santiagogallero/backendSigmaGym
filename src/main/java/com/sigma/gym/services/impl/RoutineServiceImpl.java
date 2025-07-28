package com.sigma.gym.services.impl;

import com.sigma.gym.entity.Routine;
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
    public Routine saveRoutine(Routine routine) {
        return routineRepository.save(routine);
    }

    @Override
    public List<Routine> getRoutinesByUserId(Long userId) {
        return routineRepository.findByUserId(userId);
    }

    @Override
    public Routine getRoutineById(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
    }

    @Override
    public void deleteRoutine(Long id) {
        routineRepository.deleteById(id);
    }
}
