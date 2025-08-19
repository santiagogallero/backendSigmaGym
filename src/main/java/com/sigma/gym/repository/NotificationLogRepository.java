package com.sigma.gym.repository;

import com.sigma.gym.entity.NotificationLogEntity;
import com.sigma.gym.entity.NotificationEvent;
import com.sigma.gym.entity.NotificationStatus;
import com.sigma.gym.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
    
    Page<NotificationLogEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
    
    Page<NotificationLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<NotificationLogEntity> findByStatus(NotificationStatus status);
    
    List<NotificationLogEntity> findByStatusAndNextRetryAtBefore(NotificationStatus status, LocalDateTime dateTime);
    
    @Query("SELECT nl FROM NotificationLogEntity nl WHERE nl.user = :user AND nl.eventType = :eventType AND nl.createdAt > :since")
    List<NotificationLogEntity> findRecentByUserAndEventType(
        @Param("user") UserEntity user,
        @Param("eventType") NotificationEvent eventType,
        @Param("since") LocalDateTime since
    );
    
    @Query("SELECT COUNT(nl) FROM NotificationLogEntity nl WHERE nl.user = :user AND nl.status = :status")
    long countByUserAndStatus(@Param("user") UserEntity user, @Param("status") NotificationStatus status);
    
    @Query("SELECT COUNT(nl) FROM NotificationLogEntity nl WHERE nl.user = :user AND nl.eventType = :eventType AND nl.createdAt > :since")
    long countRecentByUserAndEventType(
        @Param("user") UserEntity user,
        @Param("eventType") NotificationEvent eventType,
        @Param("since") LocalDateTime since
    );
    
    @Query("SELECT nl FROM NotificationLogEntity nl WHERE nl.status = 'RETRY_PENDING' AND nl.nextRetryAt <= :now ORDER BY nl.nextRetryAt")
    List<NotificationLogEntity> findPendingRetries(@Param("now") LocalDateTime now);
}
