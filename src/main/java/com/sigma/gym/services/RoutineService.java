package com.sigma.gym.services;

import com.sigma.gym.entity.RoutineEntity;

import java.util.List;

public interface RoutineService {
    RoutineEntity saveRoutine(RoutineEntity routine);
    List<RoutineEntity> getRoutinesByTrainerId(Long trainerId);

    RoutineEntity getRoutineById(Long id);
    void deleteRoutine(Long id);
}
