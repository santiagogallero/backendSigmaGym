package com.sigma.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sigma.gym.entity.RoutineLogEntity;

@Repository
public interface RoutineLogRepository extends JpaRepository<RoutineLogEntity, Long> {
}
