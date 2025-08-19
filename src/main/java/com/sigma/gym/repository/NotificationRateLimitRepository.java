package com.sigma.gym.repository;

import com.sigma.gym.entity.NotificationRateLimitEntity;
import com.sigma.gym.entity.NotificationEvent;
import com.sigma.gym.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRateLimitRepository extends JpaRepository<NotificationRateLimitEntity, Long> {
    
    Optional<NotificationRateLimitEntity> findByUserAndEventType(UserEntity user, NotificationEvent eventType);
    
    Optional<NotificationRateLimitEntity> findByUserIdAndEventType(Long userId, NotificationEvent eventType);
    
    @Modifying
    @Query("DELETE FROM NotificationRateLimitEntity nrl WHERE nrl.windowStart < :threshold")
    void deleteOldRateLimits(@Param("threshold") LocalDateTime threshold);
}
