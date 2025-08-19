package com.sigma.gym.listeners;

import com.sigma.gym.entity.NotificationEvent;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.events.WaitlistPromotedEvent;
import com.sigma.gym.events.WaitlistHoldExpiredEvent;
import com.sigma.gym.events.WaitlistConfirmedEvent;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.notification.core.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitlistNotificationListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Async
    @EventListener
    public void handleWaitlistPromotion(WaitlistPromotedEvent event) {
        log.info("Handling waitlist promotion event for user {} and class {}", 
            event.getUserId(), event.getClassSessionId());

        try {
            UserEntity user = userRepository.findById(event.getUserId())
                .orElse(null);
            
            if (user == null) {
                log.warn("User not found for waitlist promotion: {}", event.getUserId());
                return;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", event.getUserFirstName());
            variables.put("lastName", event.getUserLastName());
            variables.put("className", event.getClassName());
            variables.put("classType", event.getClassType());
            variables.put("startsAt", event.getClassStartsAt().format(formatter));
            variables.put("holdUntil", event.getHoldUntil().format(formatter));
            variables.put("minutesToConfirm", String.valueOf(event.getMinutesToConfirm()));
            variables.put("confirmationUrl", event.getConfirmationUrl());
            variables.put("trainerName", event.getTrainerName() != null ? event.getTrainerName() : "TBD");
            variables.put("position", String.valueOf(event.getPosition()));

            // Send notification using existing notification system
            notificationService.sendNotification(user, NotificationEvent.WAITLIST_PROMOTED, variables);
            
        } catch (Exception e) {
            log.error("Failed to send waitlist promotion notification for user {}", event.getUserId(), e);
        }
    }

    @Async
    @EventListener
    public void handleWaitlistHoldExpired(WaitlistHoldExpiredEvent event) {
        log.info("Handling waitlist hold expired event for user {} and class {}", 
            event.getUserId(), event.getClassSessionId());

        try {
            UserEntity user = userRepository.findById(event.getUserId())
                .orElse(null);
            
            if (user == null) {
                log.warn("User not found for waitlist hold expired: {}", event.getUserId());
                return;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", event.getUserFirstName());
            variables.put("lastName", event.getUserLastName());
            variables.put("className", event.getClassName());
            variables.put("startsAt", event.getClassStartsAt().format(formatter));

            // Send notification using existing notification system
            notificationService.sendNotification(user, NotificationEvent.WAITLIST_HOLD_EXPIRED, variables);
            
        } catch (Exception e) {
            log.error("Failed to send waitlist hold expired notification for user {}", event.getUserId(), e);
        }
    }

    @Async
    @EventListener
    public void handleWaitlistConfirmed(WaitlistConfirmedEvent event) {
        log.info("Handling waitlist confirmed event for user {} and class {}", 
            event.getUserId(), event.getClassSessionId());

        try {
            UserEntity user = userRepository.findById(event.getUserId())
                .orElse(null);
            
            if (user == null) {
                log.warn("User not found for waitlist confirmed: {}", event.getUserId());
                return;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", event.getUserFirstName());
            variables.put("lastName", event.getUserLastName());
            variables.put("className", event.getClassName());
            variables.put("startsAt", event.getClassStartsAt().format(formatter));
            variables.put("trainerName", event.getTrainerName() != null ? event.getTrainerName() : "TBD");
            variables.put("bookingId", String.valueOf(event.getBookingId()));
            variables.put("previousPosition", String.valueOf(event.getPreviousPosition()));

            // Send notification using existing notification system
            notificationService.sendNotification(user, NotificationEvent.WAITLIST_CONFIRMED, variables);
            
        } catch (Exception e) {
            log.error("Failed to send waitlist confirmed notification for user {}", event.getUserId(), e);
        }
    }
}
