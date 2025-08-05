package com.sigma.gym.controllers;

import com.sigma.gym.model.Progress;
import com.sigma.gym.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping
    public ResponseEntity<Progress> createProgress(@RequestBody Progress progress) {
        return ResponseEntity.ok(progressService.createProgress(progress));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Progress> getProgressById(@PathVariable Long id) {
        return ResponseEntity.ok(progressService.getProgressById(id));
    }

    @GetMapping
    public ResponseEntity<List<Progress>> getAllProgress() {
        return ResponseEntity.ok(progressService.getAllProgress());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Progress> updateProgress(@PathVariable Long id, @RequestBody Progress progress) {
        return ResponseEntity.ok(progressService.updateProgress(id, progress));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long id) {
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}
