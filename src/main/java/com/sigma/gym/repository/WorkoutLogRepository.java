package com.sigma.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sigma.gym.entity.WorkOutLogEntity;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkOutLogEntity, Long> {
    // Si necesitás después, podés agregar por ejemplo:
    // List<WorkoutLogEntity> findByUserId(Long userId);
}
