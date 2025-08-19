package com.sigma.gym.entity;

import jakarta.persistence.*;
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
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_user_id", columnList = "user_id"),
    @Index(name = "idx_booking_class_session_id", columnList = "class_session_id"),
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_created_at", columnList = "created_at")
})
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id", nullable = false)
    @NotNull(message = "Class session is required")
    private ClassSessionEntity classSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    // Link to original booking when rescheduled
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescheduled_from_booking_id")
    private BookingEntity rescheduledFromBooking;

    // For easier access and building
    @Column(name = "rescheduled_from_booking_id", insertable = false, updatable = false)
    private Long rescheduledFromBookingId;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "rescheduled_at")
    private LocalDateTime rescheduledAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "notes", length = 500)
    private String notes;

    // Helper methods
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getClassSessionId() {
        return classSession != null ? classSession.getId() : null;
    }

    public Long getRescheduledFromBookingId() {
        return rescheduledFromBooking != null ? rescheduledFromBooking.getId() : null;
    }

    public boolean isConfirmed() {
        return BookingStatus.CONFIRMED.equals(status);
    }

    public boolean isCancelled() {
        return BookingStatus.CANCELLED.equals(status);
    }

    public boolean isReprogrammed() {
        return BookingStatus.REPROGRAMMED.equals(status);
    }

    public boolean canBeCancelled() {
        return BookingStatus.CONFIRMED.equals(status);
    }

    public boolean canBeRescheduled() {
        return BookingStatus.CONFIRMED.equals(status);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
