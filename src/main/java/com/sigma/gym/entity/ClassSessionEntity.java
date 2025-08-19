package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class_sessions", indexes = {
    @Index(name = "idx_class_session_starts_at", columnList = "starts_at"),
    @Index(name = "idx_class_session_trainer_id", columnList = "trainer_id"),
    @Index(name = "idx_class_session_type", columnList = "class_type")
})
public class ClassSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @NotBlank(message = "Class type is required")
    @Column(name = "class_type", nullable = false, length = 50)
    private String classType;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    @NotNull(message = "Trainer is required")
    private UserEntity trainer;

    // Additional fields for program and location support
    @Column(name = "program_id")
    private Long programId;

    @Column(name = "coach_id")
    private Long coachId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "starts_at", nullable = false)
    @NotNull(message = "Start time is required")
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    @NotNull(message = "End time is required")
    private LocalDateTime endsAt;

    @Column(name = "capacity", nullable = false)
    @Min(value = 1, message = "Capacity must be at least 1")
    @Builder.Default
    private Integer capacity = 10;

    @Column(name = "booked_count", nullable = false)
    @Min(value = 0, message = "Booked count cannot be negative")
    @Builder.Default
    private Integer bookedCount = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public Long getTrainerId() {
        return trainer != null ? trainer.getId() : null;
    }

    public boolean hasCapacity() {
        return bookedCount < capacity;
    }

    public int getAvailableSpots() {
        return Math.max(0, capacity - bookedCount);
    }

    public boolean isFull() {
        return bookedCount >= capacity;
    }

    public void incrementBookedCount() {
        if (bookedCount < capacity) {
            bookedCount++;
        } else {
            throw new IllegalStateException("Cannot exceed class capacity");
        }
    }

    public void decrementBookedCount() {
        if (bookedCount > 0) {
            bookedCount--;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
