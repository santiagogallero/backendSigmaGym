package com.sigma.gym.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@ConfigurationProperties(prefix = "classes")
@Data
public class WaitlistProperties {
    
    private String timezone = "America/Argentina/Buenos_Aires";
    private int joinWaitlistCutoffMinutes = 15;
    private int waitlistMaxSize = 50;
    private int promotionHoldMinutes = 15;
    private boolean autoPromoteOnFreeSpot = true;
    private int defaultCapacity = 20;
    private int maxCapacity = 100;

    public ZoneId getZoneId() {
        return ZoneId.of(timezone);
    }
}
