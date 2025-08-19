package com.sigma.gym.repository;

import com.sigma.gym.entity.NotificationTemplateEntity;
import com.sigma.gym.entity.NotificationEvent;
import com.sigma.gym.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, Long> {
    
    Optional<NotificationTemplateEntity> findByEventTypeAndChannelAndIsActiveTrue(
        NotificationEvent eventType, 
        NotificationChannel channel
    );
    
    List<NotificationTemplateEntity> findByEventTypeAndIsActiveTrue(NotificationEvent eventType);
    
    List<NotificationTemplateEntity> findByChannelAndIsActiveTrue(NotificationChannel channel);
    
    List<NotificationTemplateEntity> findByIsActiveTrue();
}
