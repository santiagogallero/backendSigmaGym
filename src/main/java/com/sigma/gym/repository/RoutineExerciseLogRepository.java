package com.sigma.gym.repository;

import com.sigma.gym.entity.RoutineExerciseLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineExerciseLogRepository extends JpaRepository<RoutineExerciseLogEntity, Long> {
    List<RoutineExerciseLogEntity> findByWorkoutLogId(Long workoutLogId);
}
