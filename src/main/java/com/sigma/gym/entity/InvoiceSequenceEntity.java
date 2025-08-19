package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_sequence", indexes = {
    @Index(name = "idx_invoice_sequence_pos_series", columnList = "pos, series", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class InvoiceSequenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos", nullable = false, length = 10)
    @Builder.Default
    private String pos = "0001"; // Point of Sale

    @Column(name = "series", nullable = false, length = 5)
    @Builder.Default
    private String series = "A"; // Invoice series

    @Column(name = "last_number", nullable = false)
    @Builder.Default
    private Long lastNumber = 0L; // Last issued number

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper method to get next number
    public Long getNextNumber() {
        return lastNumber + 1;
    }

    // Helper method to format invoice number
    public String formatInvoiceNumber(Long number) {
        return String.format("%s-%s-%08d", series, pos, number);
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
