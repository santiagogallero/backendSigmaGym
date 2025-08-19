package com.sigma.gym.repository;

import com.sigma.gym.entity.NotificationPreferenceEntity;
import com.sigma.gym.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceEntity, Long> {
    
    Optional<NotificationPreferenceEntity> findByUser(UserEntity user);
    
    Optional<NotificationPreferenceEntity> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
