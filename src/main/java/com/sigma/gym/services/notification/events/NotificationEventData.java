package com.sigma.gym.services.notification.events;

import com.sigma.gym.entity.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Base notification event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventData {
    private Long userId;
    private NotificationEvent eventType;
    private Map<String, Object> variables;
}
