package com.sigma.gym.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceIssuedEvent {
    
    private Long invoiceId;
    private Long userId;
    private String invoiceNumber;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
    
    // Constructor for basic event
    public InvoiceIssuedEvent(Long invoiceId, Long userId, String invoiceNumber) {
        this.invoiceId = invoiceId;
        this.userId = userId;
        this.invoiceNumber = invoiceNumber;
        this.eventTime = LocalDateTime.now();
    }
}
