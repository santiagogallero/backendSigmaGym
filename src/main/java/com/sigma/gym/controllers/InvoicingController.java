package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.InvoicingException;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.FiscalProfileService;
import com.sigma.gym.services.InvoiceService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/invoicing")
@RequiredArgsConstructor
public class InvoicingController {

    private final FiscalProfileService fiscalProfileService;
    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    /**
     * Get current user's fiscal profile
     */
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> getMyProfile(Authentication auth) {
        try {
            UserEntity user = getUserFromAuth(auth);
            Optional<FiscalProfileDTO> profile = fiscalProfileService.getMyProfile(user.getId());
            
            if (profile.isPresent()) {
                return ResponseEntity.ok(profile.get());
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            log.error("Error getting fiscal profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "PROFILE_ERROR"));
        }
    }

    /**
     * Create or update current user's fiscal profile
     */
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> upsertMyProfile(@Valid @RequestBody UpsertFiscalProfileRequest request, 
                                           Authentication auth) {
        try {
            UserEntity user = getUserFromAuth(auth);
            FiscalProfileDTO profile = fiscalProfileService.upsertProfile(user.getId(), request);
            return ResponseEntity.ok(profile);
        } catch (InvoicingException e) {
            log.error("Error upserting fiscal profile", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Unexpected error upserting fiscal profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "PROFILE_ERROR"));
        }
    }

    /**
     * Get current user's invoices with pagination
     */
    @GetMapping("/me/invoices")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Page<InvoiceDTO>> getMyInvoices(Pageable pageable, Authentication auth) {
        try {
            UserEntity user = getUserFromAuth(auth);
            Page<InvoiceDTO> invoices = invoiceService.listUserInvoices(user.getId(), pageable);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error getting user invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get specific invoice details
     */
    @GetMapping("/me/invoices/{invoiceId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> getMyInvoice(@PathVariable Long invoiceId, Authentication auth) {
        try {
            UserEntity user = getUserFromAuth(auth);
            Optional<InvoiceDetailDTO> invoice = invoiceService.getInvoice(invoiceId, user.getId());
            
            if (invoice.isPresent()) {
                return ResponseEntity.ok(invoice.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error getting invoice details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "INVOICE_ERROR"));
        }
    }

    /**
     * Download invoice PDF
     */
    @GetMapping("/me/invoices/{invoiceId}/pdf")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> downloadInvoicePdf(@PathVariable Long invoiceId, Authentication auth) {
        try {
            UserEntity user = getUserFromAuth(auth);
            byte[] pdfBytes = invoiceService.getInvoicePdf(invoiceId, user.getId());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + invoiceId + ".pdf");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
                
        } catch (InvoicingException e) {
            log.error("Error downloading invoice PDF", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Unexpected error downloading invoice PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "PDF_ERROR"));
        }
    }

    /**
     * Admin: Void an invoice
     */
    @PostMapping("/admin/invoices/void/{invoiceId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('TRAINER')")
    public ResponseEntity<?> voidInvoice(@PathVariable Long invoiceId, 
                                       @RequestParam String reason,
                                       Authentication auth) {
        try {
            InvoiceDTO invoice = invoiceService.voidInvoice(invoiceId, reason);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            log.error("Error voiding invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), "VOID_ERROR"));
        }
    }

    /**
     * Admin: Manual invoice creation (for testing)
     */
    @PostMapping("/admin/invoices/issue")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> issueInvoice(@RequestParam Long paymentId,
                                        @RequestParam(required = false) String description,
                                        @RequestParam String amount,
                                        @RequestParam(defaultValue = "ARS") String currency,
                                        Authentication auth) {
        try {
            java.math.BigDecimal amountValue = new java.math.BigDecimal(amount);
            InvoiceDTO invoice = invoiceService.issueForPayment(paymentId, description, amountValue, currency);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (InvoicingException e) {
            log.error("Error issuing invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Unexpected error issuing invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), "INVOICE_ERROR"));
        }
    }

    private UserEntity getUserFromAuth(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Getter
    private static class ErrorResponse {
        private final String message;
        private final String errorCode;

        public ErrorResponse(String message, String errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }
    }
}
