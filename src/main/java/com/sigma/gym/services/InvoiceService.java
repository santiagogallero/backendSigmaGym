package com.sigma.gym.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.gym.DTOs.*;
import com.sigma.gym.config.InvoicingProperties;
import com.sigma.gym.entity.*;
import com.sigma.gym.events.InvoiceIssuedEvent;
import com.sigma.gym.exceptions.InvoicingException;
import com.sigma.gym.repository.*;
import com.sigma.gym.services.invoicing.InvoiceTemplateService;
import com.sigma.gym.services.invoicing.PdfRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceSequenceRepository sequenceRepository;
    private final UserRepository userRepository;
    private final FiscalProfileService fiscalProfileService;
    private final InvoiceTemplateService templateService;
    private final PdfRenderer pdfRenderer;
    private final ApplicationEventPublisher eventPublisher;
    private final InvoicingProperties properties;
    private final ObjectMapper objectMapper;

    @Transactional
    public InvoiceDTO issueForPayment(Long paymentId, String description, BigDecimal amount, String currency) {
        log.info("Issuing invoice for payment: {} with amount: {} {}", paymentId, amount, currency);

        // Validate not already invoiced
        if (invoiceRepository.existsByPaymentId(paymentId)) {
            throw new InvoicingException.AlreadyInvoicedException(
                "Invoice already exists for payment: " + paymentId);
        }

        // For now, we'll create a mock payment structure since we don't have the payment entity
        // In a real implementation, you would load the actual payment entity
        Long userId = getUserIdFromPayment(paymentId); // This would be implemented based on your payment system
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Load or create fiscal profile
        FiscalProfileEntity fiscalProfile = fiscalProfileService.getEntityByUserId(userId);
        
        // Calculate amounts
        BigDecimal subtotal = amount;
        BigDecimal taxRate = BigDecimal.valueOf(properties.getTax().getRate());
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal total = subtotal;
        
        if (properties.getTax().isEnabled()) {
            taxAmount = subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            total = subtotal.add(taxAmount);
        }

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();

        // Get issue date in configured timezone
        LocalDateTime issueDate = LocalDateTime.now(ZoneId.of(properties.getTimezone()));

        // Create fiscal snapshot
        String fiscalSnapshotJson = createFiscalSnapshot(fiscalProfile, user);

        // Create invoice entity
        InvoiceEntity invoice = InvoiceEntity.builder()
            .userId(userId)
            .paymentId(paymentId)
            .invoiceNumber(invoiceNumber)
            .invoiceType(properties.getDefaultInvoiceType())
            .currency(currency)
            .subtotal(subtotal)
            .taxAmount(taxAmount)
            .taxRate(taxRate)
            .total(total)
            .issueDate(issueDate)
            .status(InvoiceEntity.InvoiceStatus.ISSUED)
            .fiscalSnapshotJson(fiscalSnapshotJson)
            .build();

        // Generate PDF
        generateAndStorePdf(invoice, description, user);

        // Save invoice
        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);
        
        log.info("Invoice issued successfully: {} for payment: {}", savedInvoice.getInvoiceNumber(), paymentId);

        // Fire event
        eventPublisher.publishEvent(new InvoiceIssuedEvent(savedInvoice.getId(), userId, savedInvoice.getInvoiceNumber()));

        return mapToDTO(savedInvoice);
    }

    /**
     * Generate invoice only if it doesn't exist for the payment ID (idempotent operation)
     * @param paymentId Unique payment identifier
     * @param description Invoice description
     * @param amount Payment amount
     * @param currency Payment currency
     * @return InvoiceDTO if created, null if already exists
     */
    @Transactional
    public InvoiceDTO generateIfNotExists(Long paymentId, String description, BigDecimal amount, String currency) {
        log.debug("Generating invoice if not exists for payment: {} with amount: {} {}", paymentId, amount, currency);

        // Check if invoice already exists (idempotency guard)
        if (invoiceRepository.existsByPaymentId(paymentId)) {
            log.debug("Invoice already exists for payment {}, skipping generation", paymentId);
            return null;
        }

        // Generate new invoice
        return issueForPayment(paymentId, description, amount, currency);
    }

    @Transactional(readOnly = true)
    public Optional<InvoiceDetailDTO> getInvoice(Long invoiceId, Long userId) {
        log.debug("Getting invoice: {} for user: {}", invoiceId, userId);

        return invoiceRepository.findById(invoiceId)
            .filter(invoice -> invoice.getUserId().equals(userId))
            .map(this::mapToDetailDTO);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> listUserInvoices(Long userId, Pageable pageable) {
        log.debug("Listing invoices for user: {}", userId);
        
        return invoiceRepository.findByUserIdOrderByIssueDateDesc(userId, pageable)
            .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public byte[] getInvoicePdf(Long invoiceId, Long userId) {
        log.debug("Getting PDF for invoice: {} and user: {}", invoiceId, userId);

        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
            .filter(inv -> inv.getUserId().equals(userId))
            .orElseThrow(() -> new RuntimeException("Invoice not found or access denied"));

        if (invoice.getPdfBlob() != null) {
            return invoice.getPdfBlob();
        }

        if (invoice.getPdfPath() != null) {
            try {
                return Files.readAllBytes(Paths.get(invoice.getPdfPath()));
            } catch (IOException e) {
                log.error("Error reading PDF file: {}", invoice.getPdfPath(), e);
                throw new InvoicingException.PdfErrorException("Could not read PDF file");
            }
        }

        throw new InvoicingException.PdfErrorException("No PDF available for invoice");
    }

    @Transactional
    public InvoiceDTO voidInvoice(Long invoiceId, String reason) {
        log.info("Voiding invoice: {} with reason: {}", invoiceId, reason);

        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (invoice.isVoid()) {
            throw new RuntimeException("Invoice is already void");
        }

        invoice.voidInvoice(reason);
        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice voided successfully: {}", savedInvoice.getInvoiceNumber());
        
        return mapToDTO(savedInvoice);
    }

    private String generateInvoiceNumber() {
        try {
            // Find or create sequence
            InvoiceSequenceEntity sequence = sequenceRepository
                .findByPosAndSeriesForUpdate(properties.getNumbering().getPos(), 
                                           properties.getNumbering().getSeries())
                .orElse(InvoiceSequenceEntity.builder()
                    .pos(properties.getNumbering().getPos())
                    .series(properties.getNumbering().getSeries())
                    .lastNumber(0L)
                    .build());

            // Increment and format
            Long nextNumber = sequence.getNextNumber();
            sequence.setLastNumber(nextNumber);
            sequenceRepository.save(sequence);

            return sequence.formatInvoiceNumber(nextNumber);
        } catch (Exception e) {
            log.error("Error generating invoice number", e);
            throw new InvoicingException.SequenceErrorException("Could not generate invoice number", e);
        }
    }

    private void generateAndStorePdf(InvoiceEntity invoice, String description, UserEntity user) {
        try {
            // Create invoice detail for template
            InvoiceDetailDTO detail = mapToDetailDTO(invoice);
            detail.setUserName(user.getFirstName() + " " + user.getLastName());
            detail.setUserEmail(user.getEmail());
            
            // Create simple line items
            List<InvoiceDetailDTO.InvoiceLineDTO> lines = new ArrayList<>();
            lines.add(InvoiceDetailDTO.InvoiceLineDTO.builder()
                .description(description != null ? description : "Servicios Sigma Gym")
                .quantity(1)
                .unitPrice(invoice.getSubtotal())
                .amount(invoice.getSubtotal())
                .category("MEMBERSHIP")
                .build());
            detail.setLines(lines);

            // Generate HTML and PDF
            String html = templateService.generateInvoiceHtml(detail);
            byte[] pdfBytes = pdfRenderer.renderHtmlToPdf(html);

            // Store PDF based on configuration
            if ("DB".equalsIgnoreCase(properties.getStorage().getBackend())) {
                invoice.setPdfBlob(pdfBytes);
            } else {
                String pdfPath = storePdfToFileSystem(invoice.getInvoiceNumber(), pdfBytes);
                invoice.setPdfPath(pdfPath);
            }

        } catch (Exception e) {
            log.error("Error generating PDF for invoice: {}", invoice.getInvoiceNumber(), e);
            throw new InvoicingException.PdfErrorException("Failed to generate invoice PDF", e);
        }
    }

    private String storePdfToFileSystem(String invoiceNumber, byte[] pdfBytes) throws IOException {
        Path basePath = Paths.get(properties.getStorage().getBasePath());
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }

        String fileName = "invoice_" + invoiceNumber.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        Path filePath = basePath.resolve(fileName);
        
        Files.write(filePath, pdfBytes);
        
        return filePath.toString();
    }

    private String createFiscalSnapshot(FiscalProfileEntity fiscalProfile, UserEntity user) {
        try {
            if (fiscalProfile != null) {
                FiscalProfileDTO dto = FiscalProfileDTO.builder()
                    .id(fiscalProfile.getId())
                    .userId(fiscalProfile.getUserId())
                    .legalName(fiscalProfile.getLegalName())
                    .taxId(fiscalProfile.getTaxId())
                    .documentType(fiscalProfile.getDocumentType())
                    .documentNumber(fiscalProfile.getDocumentNumber())
                    .addressLine(fiscalProfile.getAddressLine())
                    .city(fiscalProfile.getCity())
                    .state(fiscalProfile.getState())
                    .postalCode(fiscalProfile.getPostalCode())
                    .country(fiscalProfile.getCountry())
                    .build();
                return objectMapper.writeValueAsString(dto);
            } else {
                // Fallback to user data
                FiscalProfileDTO fallback = FiscalProfileDTO.builder()
                    .userId(user.getId())
                    .legalName(user.getFirstName() + " " + user.getLastName())
                    .documentType("DNI")
                    .documentNumber("00000000") // Placeholder
                    .country("AR")
                    .build();
                return objectMapper.writeValueAsString(fallback);
            }
        } catch (Exception e) {
            log.error("Error creating fiscal snapshot", e);
            return "{}";
        }
    }

    private Long getUserIdFromPayment(Long paymentId) {
        // TODO: This should be implemented based on your actual payment system
        // For now, returning a placeholder. In real implementation, you would:
        // 1. Load payment entity by paymentId
        // 2. Extract userId from payment entity
        // 3. Return the userId
        
        // Placeholder implementation - in production this would query the payment table
        return 1L; // Assuming user ID 1 for development
    }

    private InvoiceDTO mapToDTO(InvoiceEntity entity) {
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

    private InvoiceDetailDTO mapToDetailDTO(InvoiceEntity entity) {
        InvoiceDetailDTO dto = InvoiceDetailDTO.builder()
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

        // Parse fiscal snapshot
        if (entity.getFiscalSnapshotJson() != null && !entity.getFiscalSnapshotJson().isEmpty()) {
            try {
                FiscalProfileDTO fiscalSnapshot = objectMapper.readValue(
                    entity.getFiscalSnapshotJson(), FiscalProfileDTO.class);
                dto.setFiscalSnapshot(fiscalSnapshot);
            } catch (Exception e) {
                log.warn("Could not parse fiscal snapshot for invoice: {}", entity.getId(), e);
            }
        }

        return dto;
    }
}
