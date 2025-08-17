package com.sigma.gym.controllers.admin;

import com.sigma.gym.model.Appointment;
import com.sigma.gym.model.AppointmentStatus;
import com.sigma.gym.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class AppointmentAdminController {
    
    private final AppointmentService appointmentService;

    @GetMapping
    public List<Appointment> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return appointmentService.getByDateTimeRange(from, to);
    }

    @PostMapping
    public Appointment create(@RequestBody Appointment req) {
        // Si viene status null -> ACTIVE por defecto
        if (req.getStatus() == null) {
            req.setStatus(AppointmentStatus.ACTIVE);
        }
        // Validar que date no sea null
        if (req.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return appointmentService.create(req);
    }

    @PatchMapping("/{id}")
    public Appointment update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LocalDateTime date = null;
        AppointmentStatus status = null;
        
        if (body.containsKey("date") && body.get("date") != null) {
            date = LocalDateTime.parse((String) body.get("date"));
        }
        
        if (body.containsKey("status") && body.get("status") != null) {
            status = AppointmentStatus.valueOf((String) body.get("status"));
        }
        
        return appointmentService.partialUpdate(id, date, status);
    }
}
