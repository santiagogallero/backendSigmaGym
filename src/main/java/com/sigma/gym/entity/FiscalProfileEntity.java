package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fiscal_profile", indexes = {
    @Index(name = "idx_fiscal_profile_user", columnList = "user_id", unique = true),
    @Index(name = "idx_fiscal_profile_tax_id", columnList = "tax_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FiscalProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @NotBlank(message = "Legal name is required")
    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Column(name = "tax_id", length = 20)
    private String taxId; // CUIT format: 20-12345678-3

    @NotBlank(message = "Document type is required")
    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType; // DNI, CUIT, CUIL

    @NotBlank(message = "Document number is required")
    @Column(name = "document_number", nullable = false, length = 20)
    private String documentNumber;

    @Column(name = "address_line", length = 300)
    private String addressLine;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 3)
    @Builder.Default
    private String country = "AR";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
