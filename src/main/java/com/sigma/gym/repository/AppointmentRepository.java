package com.sigma.gym.repository;

import com.sigma.gym.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByUser_Id(Long userId);
    List<AppointmentEntity> findByTrainer_Id(Long trainerId);
}
