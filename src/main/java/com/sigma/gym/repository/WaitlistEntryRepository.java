package com.sigma.gym.repository;

import com.sigma.gym.entity.WaitlistEntryEntity;
import com.sigma.gym.entity.WaitlistEntryEntity.WaitlistStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntryEntity, Long> {

    // Find next entry to promote (FIFO by position, then by creation time)
    Optional<WaitlistEntryEntity> findTopByClassSession_IdAndStatusOrderByPositionAscCreatedAtAsc(
        Long classSessionId, WaitlistStatus status);

    // Check if user already in waitlist for this class
    @Query("SELECT COUNT(we) > 0 FROM WaitlistEntryEntity we WHERE we.classSession.id = :classSessionId AND we.user.id = :userId AND we.status IN :statuses")
    boolean existsByClassSession_IdAndUserIdAndStatusIn(
        @Param("classSessionId") Long classSessionId, @Param("userId") Long userId, @Param("statuses") List<WaitlistStatus> statuses);

    // Get user's position in waitlist
    @Query("SELECT we FROM WaitlistEntryEntity we WHERE we.classSession.id = :classSessionId AND we.user.id = :userId AND we.status = :status")
    Optional<WaitlistEntryEntity> findByClassSession_IdAndUserIdAndStatus(
        @Param("classSessionId") Long classSessionId, @Param("userId") Long userId, @Param("status") WaitlistStatus status);

    // Count waitlist entries for a class
    int countByClassSession_IdAndStatus(Long classSessionId, WaitlistStatus status);

    // Get all waitlist entries for a class (admin view)
    List<WaitlistEntryEntity> findByClassSession_IdAndStatusOrderByPositionAscCreatedAtAsc(
        Long classSessionId, WaitlistStatus status);

    // Get user's waitlist entries
    @Query("SELECT we FROM WaitlistEntryEntity we WHERE we.user.id = :userId ORDER BY we.createdAt DESC")
    List<WaitlistEntryEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT we FROM WaitlistEntryEntity we WHERE we.user.id = :userId AND we.status IN :statuses ORDER BY we.createdAt DESC")
    List<WaitlistEntryEntity> findByUserIdAndStatusInOrderByCreatedAtDesc(
        @Param("userId") Long userId, @Param("statuses") List<WaitlistStatus> statuses);

    // Find entries with expired holds
    @Query("SELECT w FROM WaitlistEntryEntity w WHERE w.status = 'PROMOTED' AND w.holdUntil < :now")
    List<WaitlistEntryEntity> findExpiredPromotions(@Param("now") LocalDateTime now);

    // Lock entry for update during promotion/confirmation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WaitlistEntryEntity w WHERE w.id = :id")
    Optional<WaitlistEntryEntity> findByIdForUpdate(@Param("id") Long id);

    // Update positions after someone leaves
    @Modifying
    @Query("UPDATE WaitlistEntryEntity w SET w.position = w.position - 1 WHERE w.classSession.id = :classSessionId AND w.position > :position AND w.status = 'QUEUED'")
    void updatePositionsAfterLeave(@Param("classSessionId") Long classSessionId, @Param("position") Integer position);

    // Get max position for new entry
    @Query("SELECT COALESCE(MAX(w.position), 0) FROM WaitlistEntryEntity w WHERE w.classSession.id = :classSessionId AND w.status = 'QUEUED'")
    Integer findMaxPositionForClass(@Param("classSessionId") Long classSessionId);

    // Count total entries (for max size validation)
    int countByClassSession_IdAndStatusIn(Long classSessionId, List<WaitlistStatus> statuses);

    // Find user's entry by ID with ownership validation
    @Query("SELECT we FROM WaitlistEntryEntity we WHERE we.id = :entryId AND we.user.id = :userId")
    Optional<WaitlistEntryEntity> findByIdAndUserId(@Param("entryId") Long entryId, @Param("userId") Long userId);

    // Admin queries
    @Query("SELECT w FROM WaitlistEntryEntity w WHERE w.classSession.id = :classSessionId ORDER BY w.position ASC, w.createdAt ASC")
    List<WaitlistEntryEntity> findAllByClassSessionIdOrderByPosition(@Param("classSessionId") Long classSessionId);
}
