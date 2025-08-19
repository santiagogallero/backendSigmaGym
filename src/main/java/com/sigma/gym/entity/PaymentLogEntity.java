package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "payment_logs",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"external_reference"}),
           @UniqueConstraint(columnNames = {"mp_payment_id"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLogEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    private UserEntity user;
    
    @Column(name = "external_reference", nullable = false, length = 255)
    @NotBlank(message = "Referencia externa es obligatoria")
    private String externalReference;
    
    @Column(name = "mp_payment_id", length = 100)
    private String mpPaymentId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "JSON")
    private Map<String, Object> rawPayload;
    
    @Column(name = "webhook_processed", nullable = false)
    @Builder.Default
    private Boolean webhookProcessed = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PaymentStatus {
        PENDING,
        APPROVED,
        AUTHORIZED,
        IN_PROCESS,
        REJECTED,
        CANCELLED,
        REFUNDED,
        CHARGED_BACK
    }
}
