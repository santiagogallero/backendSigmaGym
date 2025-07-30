package com.sigma.gym.repository;
import com.sigma.gym.entity.RoutineEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
  @Query("SELECT r FROM Routine r WHERE r.workoutPlan.trainer.id = :trainerId")
List<RoutineEntity> findByTrainerId(@Param("trainerId") Long trainerId);
// si la rutina está asociada a un usuario
}