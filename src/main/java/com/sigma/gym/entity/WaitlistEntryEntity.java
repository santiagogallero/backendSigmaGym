package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist_entries", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"class_session_id", "user_id"}),
       indexes = {
           @Index(name = "idx_waitlist_class_position", columnList = "class_session_id, position"),
           @Index(name = "idx_waitlist_status", columnList = "status"),
           @Index(name = "idx_waitlist_hold_until", columnList = "hold_until"),
           @Index(name = "idx_waitlist_user_id", columnList = "user_id")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"classSession", "user"}) // Exclude to prevent circular references
@EqualsAndHashCode(exclude = {"classSession", "user"}) // Exclude to prevent circular references
public class WaitlistEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id", nullable = false)
    @NotNull(message = "Class session is required")
    private ClassSessionEntity classSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private UserEntity user;

    @Column(name = "position", nullable = false)
    @NotNull(message = "Position is required")
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private WaitlistStatus status = WaitlistStatus.QUEUED;

    @Column(name = "promoted_at")
    private LocalDateTime promotedAt;

    @Column(name = "hold_until")
    private LocalDateTime holdUntil;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public Long getClassSessionId() {
        return classSession != null ? classSession.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public boolean isQueued() {
        return WaitlistStatus.QUEUED.equals(status);
    }

    public boolean isPromoted() {
        return WaitlistStatus.PROMOTED.equals(status);
    }

    public boolean isHoldExpired() {
        return isPromoted() && holdUntil != null && LocalDateTime.now().isAfter(holdUntil);
    }

    public boolean canConfirm() {
        return isPromoted() && holdUntil != null && LocalDateTime.now().isBefore(holdUntil);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum WaitlistStatus {
        QUEUED,     // Waiting in queue
        PROMOTED,   // Promoted and has time to confirm
        EXPIRED,    // Failed to confirm within hold window
        LEFT,       // User left the waitlist voluntarily
        CONFIRMED   // User confirmed and got the booking
    }
}
