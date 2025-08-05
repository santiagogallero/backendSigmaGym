package com.sigma.gym.controllers;

import com.sigma.gym.model.WorkoutLog;
import com.sigma.gym.services.WorkoutLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-logs")
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    public WorkoutLogController(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }

    @PostMapping
    public ResponseEntity<WorkoutLog> createWorkoutLog(@RequestBody WorkoutLog log) {
        return ResponseEntity.ok(workoutLogService.createWorkoutLog(log));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutLog> getWorkoutLogById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutLogService.getWorkoutLogById(id));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutLog>> getAllWorkoutLogs() {
        return ResponseEntity.ok(workoutLogService.getAllWorkoutLogs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutLog> updateWorkoutLog(@PathVariable Long id, @RequestBody WorkoutLog log) {
        return ResponseEntity.ok(workoutLogService.updateWorkoutLog(id, log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutLog(@PathVariable Long id) {
        workoutLogService.deleteWorkoutLog(id);
        return ResponseEntity.noContent().build();
    }
}
