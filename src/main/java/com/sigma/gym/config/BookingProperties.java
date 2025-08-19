package com.sigma.gym.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "booking")
@Data
public class BookingProperties {
    
    private String timezone = "America/Argentina/Buenos_Aires";
    private int cancelWindowHours = 2;
    private int rescheduleWindowHours = 2;
    private int monthlyCancelLimit = 6;
    private int monthlyRescheduleLimit = 4;
    private int noShowMarkAfterMinutes = 10;
    
    // Alternative getter methods for consistency
    public int getMaxCancellationsPerMonth() {
        return monthlyCancelLimit;
    }
    
    public int getMaxReschedulesPerMonth() {
        return monthlyRescheduleLimit;
    }
}
