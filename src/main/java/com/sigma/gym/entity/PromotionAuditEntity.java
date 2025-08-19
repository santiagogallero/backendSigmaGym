package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_audit", indexes = {
    @Index(name = "idx_promotion_audit_entry_id", columnList = "waitlist_entry_id"),
    @Index(name = "idx_promotion_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_promotion_audit_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waitlist_entry_id", nullable = false)
    @NotNull(message = "Waitlist entry is required")
    private WaitlistEntryEntity waitlistEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id", nullable = false)
    @NotNull(message = "Class session is required")
    private ClassSessionEntity classSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    @NotNull(message = "Action is required")
    private PromotionAction action;

    @Column(name = "previous_position")
    private Integer previousPosition;

    @Column(name = "promoted_at")
    private LocalDateTime promotedAt;

    @Column(name = "hold_until")
    private LocalDateTime holdUntil;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "metadata_json", columnDefinition = "JSON")
    private String metadataJson;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Helper methods
    public Long getWaitlistEntryId() {
        return waitlistEntry != null ? waitlistEntry.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getClassSessionId() {
        return classSession != null ? classSession.getId() : null;
    }

    public enum PromotionAction {
        PROMOTED,       // User was promoted from waitlist
        CONFIRMED,      // User confirmed their promotion
        EXPIRED,        // Promotion expired without confirmation
        SKIPPED         // User was skipped (e.g., membership frozen)
    }
}
