package com.sigma.gym.repository;

import com.sigma.gym.entity.PaymentLogEntity;
import com.sigma.gym.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLogEntity, Long> {
    
    Optional<PaymentLogEntity> findByExternalReference(String externalReference);
    
    Optional<PaymentLogEntity> findByMpPaymentId(String mpPaymentId);
    
    List<PaymentLogEntity> findByUser(UserEntity user);
    
    List<PaymentLogEntity> findByUserId(Long userId);
    
    List<PaymentLogEntity> findByStatus(PaymentLogEntity.PaymentStatus status);
    
    List<PaymentLogEntity> findByWebhookProcessedFalse();
    
    @Query("SELECT pl FROM PaymentLogEntity pl WHERE pl.createdAt >= :startDate AND pl.createdAt <= :endDate")
    List<PaymentLogEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT pl FROM PaymentLogEntity pl WHERE pl.user = :user ORDER BY pl.createdAt DESC")
    List<PaymentLogEntity> findByUserOrderByCreatedAtDesc(@Param("user") UserEntity user);
}
