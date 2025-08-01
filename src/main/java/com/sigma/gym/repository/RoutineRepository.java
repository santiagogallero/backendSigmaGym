package com.sigma.gym.repository;

import com.sigma.gym.entity.RoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {

    // ❌ PROBLEMA: Si RoutineEntity no tiene campo 'trainerId' directo
    // List<RoutineEntity> findByTrainerId(Long trainerId);

    // ✅ OPCIONES CORRECTAS:

    // Opción 1: Si hay relación con WorkoutPlan que tiene trainer
    List<RoutineEntity> findByWorkoutPlanTrainerId(Long trainerId);

    // Opción 2: Si hay relación directa con trainer (poco probable)
    // List<RoutineEntity> findByTrainerId(Long trainerId);

    // Opción 3: Por WorkoutPlan
    List<RoutineEntity> findByWorkoutPlanId(Long workoutPlanId);

    // Opción 4: Por día de la semana
    List<RoutineEntity> findByDayOfWeek(String dayOfWeek);

    // Opción 5: Métodos básicos seguros
    List<RoutineEntity> findByNameContainingIgnoreCase(String name);
}