package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.entity.FiscalProfileEntity;
import com.sigma.gym.entity.InvoiceEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoicingMapper {

    public FiscalProfileDTO mapFiscalProfileToDTO(FiscalProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return FiscalProfileDTO.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .legalName(entity.getLegalName())
            .taxId(entity.getTaxId())
            .documentType(entity.getDocumentType())
            .documentNumber(entity.getDocumentNumber())
            .addressLine(entity.getAddressLine())
            .city(entity.getCity())
            .state(entity.getState())
            .postalCode(entity.getPostalCode())
            .country(entity.getCountry())
            .build();
    }

    public InvoiceDTO mapInvoiceToDTO(InvoiceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return InvoiceDTO.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .paymentId(entity.getPaymentId())
            .invoiceNumber(entity.getInvoiceNumber())
            .invoiceType(entity.getInvoiceType())
            .currency(entity.getCurrency())
            .subtotal(entity.getSubtotal())
            .taxAmount(entity.getTaxAmount())
            .taxRate(entity.getTaxRate())
            .total(entity.getTotal())
            .issueDate(entity.getIssueDate())
            .status(entity.getStatus())
            .voidReason(entity.getVoidReason())
            .build();
    }

    public InvoiceDetailDTO mapInvoiceToDetailDTO(InvoiceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return InvoiceDetailDTO.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .paymentId(entity.getPaymentId())
            .invoiceNumber(entity.getInvoiceNumber())
            .invoiceType(entity.getInvoiceType())
            .currency(entity.getCurrency())
            .subtotal(entity.getSubtotal())
            .taxAmount(entity.getTaxAmount())
            .taxRate(entity.getTaxRate())
            .total(entity.getTotal())
            .issueDate(entity.getIssueDate())
            .status(entity.getStatus())
            .voidReason(entity.getVoidReason())
            .build();
    }

    public FiscalProfileEntity mapUpsertRequestToEntity(UpsertFiscalProfileRequest request, Long userId) {
        if (request == null) {
            return null;
        }
        
        return FiscalProfileEntity.builder()
            .userId(userId)
            .legalName(request.getLegalName())
            .taxId(request.getTaxId())
            .documentType(request.getDocumentType())
            .documentNumber(request.getDocumentNumber())
            .addressLine(request.getAddressLine())
            .city(request.getCity())
            .state(request.getState())
            .postalCode(request.getPostalCode())
            .country(request.getCountry())
            .build();
    }

    public void updateEntityFromRequest(FiscalProfileEntity entity, UpsertFiscalProfileRequest request) {
        if (entity == null || request == null) {
            return;
        }
        
        entity.setLegalName(request.getLegalName());
        entity.setTaxId(request.getTaxId());
        entity.setDocumentType(request.getDocumentType());
        entity.setDocumentNumber(request.getDocumentNumber());
        entity.setAddressLine(request.getAddressLine());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setPostalCode(request.getPostalCode());
        entity.setCountry(request.getCountry());
    }
}
