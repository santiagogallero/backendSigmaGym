package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "memberships", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @NotNull(message = "Plan es obligatorio")
    private MembershipPlanEntity plan;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.NONE;
    
    @Column(name = "last_payment_id", length = 100)
    private String lastPaymentId;
    
    @Column(name = "external_reference", length = 255)
    private String externalReference;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Campos para congelamiento de membresía
    @Column(name = "freeze_start")
    private LocalDateTime freezeStart;
    
    @Column(name = "freeze_end")
    private LocalDateTime freezeEnd;
    
    @Column(name = "freezes_used_this_year", nullable = false)
    @Builder.Default
    private Integer freezesUsedThisYear = 0;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum MembershipStatus {
        NONE,      // No hay membresía activa
        PENDING,   // Pago pendiente
        ACTIVE,    // Membresía activa
        FROZEN,    // Membresía congelada
        EXPIRED,   // Membresía expirada
        CANCELED   // Membresía cancelada
    }
}
