package com.sigma.gym.controllers;

import com.sigma.gym.model.RoutineProgress;
import com.sigma.gym.services.RoutineProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routine-progress")
public class RoutineProgressController {

    @Autowired
    private RoutineProgressService service;

    @PostMapping
    public ResponseEntity<RoutineProgress> create(@RequestBody RoutineProgress progress) {
        return ResponseEntity.ok(service.createRoutineProgress(progress));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineProgress> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRoutineProgressById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoutineProgress>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getRoutineProgressByUser(userId));
    }

    @GetMapping("/routine/{routineId}")
    public ResponseEntity<List<RoutineProgress>> getByRoutine(@PathVariable Long routineId) {
        return ResponseEntity.ok(service.getRoutineProgressByRoutine(routineId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutineProgress> update(@PathVariable Long id, @RequestBody RoutineProgress progress) {
        return ResponseEntity.ok(service.updateRoutineProgress(id, progress));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRoutineProgress(id);
        return ResponseEntity.noContent().build();
    }
}
