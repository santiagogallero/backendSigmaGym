package com.sigma.gym.DTOs;

import com.sigma.gym.entity.InvoiceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDetailDTO {
    
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
    
    // Additional details
    private List<InvoiceLineDTO> lines;
    private FiscalProfileDTO fiscalSnapshot;
    private String userEmail;
    private String userName;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceLineDTO {
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal amount;
        private String category;
    }
}
