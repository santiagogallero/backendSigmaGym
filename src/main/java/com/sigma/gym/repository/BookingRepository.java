package com.sigma.gym.repository;

import com.sigma.gym.entity.BookingEntity;
import com.sigma.gym.entity.BookingStatus;
import com.sigma.gym.entity.UserEntity;
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
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("SELECT b FROM BookingEntity b WHERE b.id = :id AND b.user.id = :userId")
    Optional<BookingEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BookingEntity b WHERE b.id = :id")
    Optional<BookingEntity> findByIdForUpdate(@Param("id") Long id);

    List<BookingEntity> findByUserAndStatus(UserEntity user, BookingStatus status);

    List<BookingEntity> findByClassSession_IdAndStatus(Long classSessionId, BookingStatus status);

    @Query("SELECT b FROM BookingEntity b WHERE b.user.id = :userId AND b.status = :status AND b.createdAt BETWEEN :startDate AND :endDate")
    List<BookingEntity> findByUserIdAndStatusBetweenDates(
        @Param("userId") Long userId,
        @Param("status") BookingStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.user.id = :userId AND b.status IN :statuses AND b.createdAt BETWEEN :startDate AND :endDate")
    long countByUserIdAndStatusInBetweenDates(
        @Param("userId") Long userId,
        @Param("statuses") List<BookingStatus> statuses,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.user.id = :userId AND b.status = 'CANCELLED' AND b.cancelledAt BETWEEN :startOfMonth AND :endOfMonth")
    long countCancelActionsByUserInMonth(
        @Param("userId") Long userId,
        @Param("startOfMonth") LocalDateTime startOfMonth,
        @Param("endOfMonth") LocalDateTime endOfMonth
    );

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.user.id = :userId AND b.status = 'REPROGRAMMED' AND b.rescheduledAt BETWEEN :startOfMonth AND :endOfMonth")
    long countRescheduleActionsByUserInMonth(
        @Param("userId") Long userId,
        @Param("startOfMonth") LocalDateTime startOfMonth,
        @Param("endOfMonth") LocalDateTime endOfMonth
    );

    @Query("SELECT b FROM BookingEntity b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    Page<BookingEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM BookingEntity b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<BookingEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT b FROM BookingEntity b WHERE b.classSession.id = :classSessionId ORDER BY b.createdAt ASC")
    List<BookingEntity> findByClassSessionIdOrderByCreatedAtAsc(@Param("classSessionId") Long classSessionId);

    // Check if user has a booking for specific class session
    @Query("SELECT COUNT(b) > 0 FROM BookingEntity b WHERE b.user.id = :userId AND b.classSession.id = :classSessionId AND b.status = :status")
    boolean existsByUserIdAndClassSessionIdAndStatus(@Param("userId") Long userId, @Param("classSessionId") Long classSessionId, @Param("status") BookingStatus status);
}
