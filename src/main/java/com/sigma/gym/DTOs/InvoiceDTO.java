package com.sigma.gym.DTOs;

import com.sigma.gym.entity.InvoiceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    
    private Long id;
    private Long userId;
    private Long paymentId;
    private String invoiceNumber;
    private String invoiceType;
    private String currency;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private BigDecimal total;
    private LocalDateTime issueDate;
    private InvoiceEntity.InvoiceStatus status;
    private String voidReason;
}
