package com.sigma.gym.repository;

import com.sigma.gym.entity.RoutineProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineProgressRepository extends JpaRepository<RoutineProgressEntity, Long> {
    List<RoutineProgressEntity> findByUser_Id(Long userId);
    List<RoutineProgressEntity> findByRoutine_Id(Long routineId);
}
