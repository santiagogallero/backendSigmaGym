package com.sigma.gym.scheduler;

import com.sigma.gym.services.WaitlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "sigmagym.features.waitlist", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class WaitlistScheduler {

    private final WaitlistService waitlistService;

    /**
     * Process expired holds every minute
     * Finds promoted entries where hold time has expired and promotes next user
     */
    @Scheduled(fixedDelay = 60000) // 1 minute
    public void processExpiredHolds() {
        log.debug("Running scheduled task: processExpiredHolds");
        
        try {
            waitlistService.processExpiredHolds();
        } catch (Exception e) {
            log.error("Error processing expired holds in scheduled task", e);
        }
    }

    /**
     * Optional: Clean up old waitlist entries
     * Remove entries older than 30 days that are in final states
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldWaitlistEntries() {
        log.info("Running scheduled cleanup of old waitlist entries");
        
        try {
            // Implementation would go here - clean up EXPIRED, LEFT, CONFIRMED entries older than X days
            // This prevents the waitlist tables from growing indefinitely
        } catch (Exception e) {
            log.error("Error cleaning up old waitlist entries", e);
        }
    }
}
