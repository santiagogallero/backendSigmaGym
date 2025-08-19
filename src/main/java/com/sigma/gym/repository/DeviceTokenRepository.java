package com.sigma.gym.repository;

import com.sigma.gym.entity.DeviceTokenEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.DevicePlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceTokenEntity, Long> {
    
    List<DeviceTokenEntity> findByUserAndIsActiveTrue(UserEntity user);
    
    List<DeviceTokenEntity> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<DeviceTokenEntity> findByUserAndToken(UserEntity user, String token);
    
    List<DeviceTokenEntity> findByUserAndPlatformAndIsActiveTrue(UserEntity user, DevicePlatform platform);
    
    @Modifying
    @Query("UPDATE DeviceTokenEntity d SET d.isActive = false WHERE d.user = :user AND d.token = :token")
    void deactivateToken(@Param("user") UserEntity user, @Param("token") String token);
    
    @Modifying
    @Query("UPDATE DeviceTokenEntity d SET d.isActive = false WHERE d.lastSeenAt < :threshold")
    void deactivateStaleTokens(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT COUNT(d) FROM DeviceTokenEntity d WHERE d.user = :user AND d.isActive = true")
    long countActiveTokensByUser(@Param("user") UserEntity user);
}
