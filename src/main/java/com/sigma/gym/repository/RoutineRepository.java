package com.sigma.gym.repository;
import com.sigma.gym.entity.Routine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUserId(Long userId); // si la rutina está asociada a un usuario
}