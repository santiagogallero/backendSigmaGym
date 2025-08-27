package com.sigma.gym.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/waitlist")
@ConditionalOnProperty(prefix = "sigmagym.features.waitlist", name = "enabled", havingValue = "false")
public class WaitlistDisabledController {

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD })
    public ResponseEntity<ErrorResponse> disabled() {
        // Downgrade to DEBUG to evitar ruido en logs cuando el frontend consulta endpoints inexistentes
        if (log.isDebugEnabled()) {
            log.debug("Waitlist feature is disabled - returning 410");
        }
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse("Waitlist feature is disabled", "FEATURE_DISABLED"));
    }

    public record ErrorResponse(String message, String errorCode) {}
}
