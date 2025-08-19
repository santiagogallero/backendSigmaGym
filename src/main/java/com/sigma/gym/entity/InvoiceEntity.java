package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice", indexes = {
    @Index(name = "idx_invoice_user", columnList = "user_id"),
    @Index(name = "idx_invoice_payment", columnList = "payment_id", unique = true),
    @Index(name = "idx_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_invoice_status", columnList = "status"),
    @Index(name = "idx_invoice_issue_date", columnList = "issue_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "payment_id", nullable = false, unique = true)
    private Long paymentId;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber; // A-0001-00000042

    @Column(name = "invoice_type", nullable = false, length = 20)
    @Builder.Default
    private String invoiceType = "RECEIPT"; // A, B, C, RECEIPT

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "ARS";

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Subtotal must be positive")
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Tax amount must be positive")
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Tax rate must be positive")
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.valueOf(21.0);

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Total must be positive")
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "issue_date", nullable = false)
    @NotNull(message = "Issue date is required")
    private LocalDateTime issueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.ISSUED;

    @Column(name = "fiscal_snapshot_json", columnDefinition = "TEXT")
    private String fiscalSnapshotJson; // Denormalized copy of FiscalProfile at issue time

    @Column(name = "pdf_path", length = 500)
    private String pdfPath; // For FS storage

    @Lob
    @Column(name = "pdf_blob")
    private byte[] pdfBlob; // For DB storage

    @Column(name = "void_reason", length = 500)
    private String voidReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum InvoiceStatus {
        ISSUED,
        VOID
    }

    // Helper methods
    public boolean isVoid() {
        return status == InvoiceStatus.VOID;
    }

    public void voidInvoice(String reason) {
        this.status = InvoiceStatus.VOID;
        this.voidReason = reason;
    }
}
