package com.sigma.gym.repository;

import com.sigma.gym.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findByUser_Id(Long userId);
    List<AttendanceEntity> findByDate(LocalDate date);
}
