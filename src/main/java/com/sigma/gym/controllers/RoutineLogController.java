package com.sigma.gym.controllers;

import com.sigma.gym.model.RoutineLog;
import com.sigma.gym.services.RoutineLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine-logs")
public class RoutineLogController {

    private final RoutineLogService routineLogService;

    public RoutineLogController(RoutineLogService routineLogService) {
        this.routineLogService = routineLogService;
    }

    @PostMapping
    public ResponseEntity<RoutineLog> createRoutineLog(@RequestBody RoutineLog log) {
        return ResponseEntity.ok(routineLogService.createRoutineLog(log));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineLog> getRoutineLogById(@PathVariable Long id) {
        return ResponseEntity.ok(routineLogService.getRoutineLogById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoutineLog>> getAllRoutineLogs() {
        return ResponseEntity.ok(routineLogService.getAllRoutineLogs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutineLog> updateRoutineLog(@PathVariable Long id, @RequestBody RoutineLog log) {
        return ResponseEntity.ok(routineLogService.updateRoutineLog(id, log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutineLog(@PathVariable Long id) {
        routineLogService.deleteRoutineLog(id);
        return ResponseEntity.noContent().build();
    }
}
