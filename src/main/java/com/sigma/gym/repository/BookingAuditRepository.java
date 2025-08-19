package com.sigma.gym.repository;

import com.sigma.gym.entity.BookingAuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface BookingAuditRepository extends JpaRepository<BookingAuditEntity, Long> {

    @Query("SELECT ba FROM BookingAuditEntity ba WHERE ba.booking.id = :bookingId ORDER BY ba.createdAt DESC")
    List<BookingAuditEntity> findByBookingIdOrderByCreatedAtDesc(@Param("bookingId") Long bookingId);

    @Query("SELECT ba FROM BookingAuditEntity ba WHERE ba.user.id = :userId ORDER BY ba.createdAt DESC")
    Page<BookingAuditEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ba FROM BookingAuditEntity ba WHERE ba.user.id = :userId AND ba.action = :action AND ba.createdAt BETWEEN :startDate AND :endDate")
    List<BookingAuditEntity> findByUserIdAndActionBetweenDates(
        @Param("userId") Long userId,
        @Param("action") BookingAuditEntity.BookingAction action,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(ba) FROM BookingAuditEntity ba WHERE ba.user.id = :userId AND ba.action = 'CANCELLED' AND YEAR(ba.createdAt) = :year AND MONTH(ba.createdAt) = :month")
    int countCancelActionsByUserInMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(ba) FROM BookingAuditEntity ba WHERE ba.user.id = :userId AND ba.action = 'RESCHEDULED' AND YEAR(ba.createdAt) = :year AND MONTH(ba.createdAt) = :month")
    int countRescheduleActionsByUserInMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // Convenience methods for YearMonth
    default int countCancelActionsByUserInMonth(Long userId, YearMonth month) {
        return countCancelActionsByUserInMonth(userId, month.getYear(), month.getMonthValue());
    }

    default int countRescheduleActionsByUserInMonth(Long userId, YearMonth month) {
        return countRescheduleActionsByUserInMonth(userId, month.getYear(), month.getMonthValue());
    }
}
