package com.sigma.gym.repository;

import com.sigma.gym.entity.MembershipEntity;
import com.sigma.gym.entity.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity, Long> {
    
    Optional<MembershipEntity> findByUser(UserEntity user);
    
    Optional<MembershipEntity> findByUserId(Long userId);
    
    Optional<MembershipEntity> findByLastPaymentId(String paymentId);
    
    Optional<MembershipEntity> findByExternalReference(String externalReference);
    
    List<MembershipEntity> findByStatus(MembershipEntity.MembershipStatus status);
    
    @Query("SELECT m FROM MembershipEntity m WHERE m.expirationDate <= :now AND m.status = 'ACTIVE'")
    List<MembershipEntity> findExpiredActiveMemberships(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(m) FROM MembershipEntity m WHERE m.status = 'ACTIVE'")
    long countActiveMemberships();
    
    @Query("SELECT m FROM MembershipEntity m WHERE m.user.email = :email")
    Optional<MembershipEntity> findByUserEmail(@Param("email") String email);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MembershipEntity m WHERE m.user.email = :email")
    Optional<MembershipEntity> findByUserEmailForUpdate(@Param("email") String email);
    
    @Query("SELECT m FROM MembershipEntity m WHERE m.status = 'FROZEN' AND m.freezeEnd <= :now")
    List<MembershipEntity> findFrozenMembershipsToUnfreeze(@Param("now") LocalDateTime now);
}
