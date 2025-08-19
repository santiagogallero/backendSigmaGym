package com.sigma.gym.repository;

import com.sigma.gym.entity.ClassSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSessionEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM ClassSessionEntity cs WHERE cs.id = :id")
    Optional<ClassSessionEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT cs FROM ClassSessionEntity cs WHERE cs.trainer.id = :trainerId AND cs.isActive = true")
    List<ClassSessionEntity> findByTrainerIdAndIsActiveTrue(@Param("trainerId") Long trainerId);

    List<ClassSessionEntity> findByStartsAtBetweenAndIsActiveTrue(LocalDateTime start, LocalDateTime end);

    @Query("SELECT cs FROM ClassSessionEntity cs WHERE cs.isActive = true AND cs.startsAt >= :now ORDER BY cs.startsAt ASC")
    Page<ClassSessionEntity> findUpcomingClasses(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT cs FROM ClassSessionEntity cs WHERE cs.isActive = true AND cs.bookedCount < cs.capacity AND cs.startsAt >= :now ORDER BY cs.startsAt ASC")
    List<ClassSessionEntity> findAvailableClasses(@Param("now") LocalDateTime now);

    List<ClassSessionEntity> findByClassTypeAndIsActiveTrue(String classType);
}
