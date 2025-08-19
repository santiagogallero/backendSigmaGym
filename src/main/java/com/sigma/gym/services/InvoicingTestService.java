package com.sigma.gym.services;

import com.sigma.gym.DTOs.FiscalProfileDTO;
import com.sigma.gym.DTOs.InvoiceDTO;
import com.sigma.gym.DTOs.UpsertFiscalProfileRequest;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Test service to demonstrate invoicing functionality
 * This runs on application startup when the 'test' profile is active
 */
@Slf4j
@Component
@Profile("test")
@RequiredArgsConstructor
public class InvoicingTestService implements CommandLineRunner {

    private final FiscalProfileService fiscalProfileService;
    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running invoicing system tests...");

        try {
            // Test fiscal profile creation
            testFiscalProfile();
            
            // Test invoice generation
            testInvoiceGeneration();
            
            log.info("Invoicing system tests completed successfully!");
            
        } catch (Exception e) {
            log.error("Invoicing system tests failed", e);
        }
    }

    private void testFiscalProfile() {
        log.info("Testing fiscal profile creation...");

        try {
            // Find first user or skip test
            UserEntity testUser = userRepository.findAll().stream()
                .findFirst()
                .orElse(null);

            if (testUser == null) {
                log.warn("No users found, skipping fiscal profile test");
                return;
            }

            // Create test fiscal profile
            UpsertFiscalProfileRequest request = UpsertFiscalProfileRequest.builder()
                .legalName("Juan Pérez")
                .taxId("20-12345678-3")
                .documentType("DNI")
                .documentNumber("12345678")
                .addressLine("Av. Siempre Viva 742")
                .city("CABA")
                .state("Buenos Aires")
                .postalCode("1000")
                .country("AR")
                .build();

            FiscalProfileDTO profile = fiscalProfileService.upsertProfile(testUser.getId(), request);
            log.info("Fiscal profile created successfully: {}", profile.getLegalName());

        } catch (Exception e) {
            log.error("Fiscal profile test failed", e);
        }
    }

    private void testInvoiceGeneration() {
        log.info("Testing invoice generation...");

        try {
            // Generate test invoice
            Long testPaymentId = 99999L; // Mock payment ID
            String description = "Test Membership - Sigma Gym";
            BigDecimal amount = new BigDecimal("5000.00");
            String currency = "ARS";

            InvoiceDTO invoice = invoiceService.issueForPayment(testPaymentId, description, amount, currency);
            
            log.info("Test invoice generated successfully: {} - Total: {} {}", 
                invoice.getInvoiceNumber(), invoice.getTotal(), invoice.getCurrency());

        } catch (Exception e) {
            log.error("Invoice generation test failed", e);
        }
    }
}
