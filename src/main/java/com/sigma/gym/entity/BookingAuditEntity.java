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
@Table(name = "booking_audit", indexes = {
    @Index(name = "idx_booking_audit_booking_id", columnList = "booking_id"),
    @Index(name = "idx_booking_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_booking_audit_action", columnList = "action"),
    @Index(name = "idx_booking_audit_created_at", columnList = "created_at")
})
public class BookingAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @NotNull(message = "Booking is required")
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id", nullable = false)
    @NotNull(message = "Actor user is required")
    private UserEntity actorUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    @NotNull(message = "Action is required")
    private BookingAction action;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "metadata_json", columnDefinition = "JSON")
    private String metadataJson;

    @Column(name = "previous_status", length = 20)
    private String previousStatus;

    @Column(name = "new_status", length = 20)
    private String newStatus;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Helper methods
    public Long getBookingId() {
        return booking != null ? booking.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getActorUserId() {
        return actorUser != null ? actorUser.getId() : null;
    }

    public enum BookingAction {
        CREATED,
        CANCELLED,
        RESCHEDULED,
        NO_SHOW_MARKED,
        ADMIN_OVERRIDE
    }
}
