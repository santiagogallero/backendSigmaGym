package com.sigma.gym.repository;

import com.sigma.gym.entity.PromotionAuditEntity;
import com.sigma.gym.entity.PromotionAuditEntity.PromotionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionAuditRepository extends JpaRepository<PromotionAuditEntity, Long> {

    // Get promotion history for a user
    @Query("SELECT pa FROM PromotionAuditEntity pa WHERE pa.user.id = :userId ORDER BY pa.createdAt DESC")
    List<PromotionAuditEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // Get promotion history for a class session
    @Query("SELECT pa FROM PromotionAuditEntity pa WHERE pa.classSession.id = :classSessionId ORDER BY pa.createdAt DESC")
    List<PromotionAuditEntity> findByClassSessionIdOrderByCreatedAtDesc(@Param("classSessionId") Long classSessionId);

    // Get promotion history for a waitlist entry
    @Query("SELECT pa FROM PromotionAuditEntity pa WHERE pa.waitlistEntry.id = :waitlistEntryId ORDER BY pa.createdAt DESC")
    List<PromotionAuditEntity> findByWaitlistEntryIdOrderByCreatedAtDesc(@Param("waitlistEntryId") Long waitlistEntryId);

    // Count promotions by action type within date range
    @Query("SELECT COUNT(p) FROM PromotionAuditEntity p WHERE p.user.id = :userId AND p.action = :action AND p.createdAt BETWEEN :startDate AND :endDate")
    long countByUserIdAndActionBetweenDates(
        @Param("userId") Long userId, 
        @Param("action") PromotionAction action,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    // Get recent promotion statistics
    @Query("SELECT p.action, COUNT(p) FROM PromotionAuditEntity p WHERE p.createdAt >= :since GROUP BY p.action")
    List<Object[]> getPromotionStatsSince(@Param("since") LocalDateTime since);
}
