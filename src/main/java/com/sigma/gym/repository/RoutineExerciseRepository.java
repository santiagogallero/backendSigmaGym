package com.sigma.gym.repository;

import com.sigma.gym.entity.RoutineExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExerciseEntity, Long> {
    List<RoutineExerciseEntity> findByRoutine_Id(Long routineId);

}
