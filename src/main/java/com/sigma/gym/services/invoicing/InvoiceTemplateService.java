package com.sigma.gym.services.invoicing;

import com.sigma.gym.DTOs.InvoiceDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class InvoiceTemplateService {
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Generate HTML content for invoice PDF
     * This is a simplified implementation using string templates
     */
    public String generateInvoiceHtml(InvoiceDetailDTO invoice) {
        log.debug("Generating invoice HTML for invoice: {}", invoice.getInvoiceNumber());
        
        try {
            String html = generateSimpleInvoiceHtml(invoice);
            log.debug("Invoice HTML generated successfully, length: {}", html.length());
            return html;
        } catch (Exception e) {
            log.error("Error generating invoice HTML for invoice: {}", invoice.getInvoiceNumber(), e);
            throw new RuntimeException("Failed to generate invoice HTML", e);
        }
    }
    
    private String generateSimpleInvoiceHtml(InvoiceDetailDTO invoice) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Invoice ").append(invoice.getInvoiceNumber()).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append(".header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 20px; }\n");
        html.append(".company { font-size: 24px; font-weight: bold; color: #333; }\n");
        html.append(".invoice-info { margin: 20px 0; }\n");
        html.append(".customer-info { margin: 20px 0; }\n");
        html.append(".line-items { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        html.append(".line-items th, .line-items td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append(".line-items th { background-color: #f2f2f2; }\n");
        html.append(".totals { text-align: right; margin: 20px 0; }\n");
        html.append(".total-row { font-weight: bold; font-size: 18px; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        // Header
        html.append("<div class='header'>\n");
        html.append("<div class='company'>Sigma Gym</div>\n");
        html.append("<div>Dirección del Gimnasio</div>\n");
        html.append("<div>+54 11 1234-5678 | info@sigmagym.app</div>\n");
        html.append("</div>\n");
        
        // Invoice info
        html.append("<div class='invoice-info'>\n");
        html.append("<h2>").append(invoice.getInvoiceType()).append(" ").append(invoice.getInvoiceNumber()).append("</h2>\n");
        html.append("<p><strong>Fecha:</strong> ").append(invoice.getIssueDate().format(dateFormatter)).append("</p>\n");
        html.append("<p><strong>Estado:</strong> ").append(invoice.getStatus().toString()).append("</p>\n");
        html.append("</div>\n");
        
        // Customer info
        html.append("<div class='customer-info'>\n");
        html.append("<h3>Cliente</h3>\n");
        if (invoice.getFiscalSnapshot() != null) {
            html.append("<p><strong>Razón Social:</strong> ").append(invoice.getFiscalSnapshot().getLegalName()).append("</p>\n");
            if (invoice.getFiscalSnapshot().getTaxId() != null) {
                html.append("<p><strong>CUIT:</strong> ").append(invoice.getFiscalSnapshot().getTaxId()).append("</p>\n");
            }
            html.append("<p><strong>Documento:</strong> ").append(invoice.getFiscalSnapshot().getDocumentType())
                .append(" ").append(invoice.getFiscalSnapshot().getDocumentNumber()).append("</p>\n");
            if (invoice.getFiscalSnapshot().getAddressLine() != null) {
                html.append("<p><strong>Dirección:</strong> ").append(invoice.getFiscalSnapshot().getAddressLine()).append("</p>\n");
            }
        } else {
            html.append("<p><strong>Cliente:</strong> ").append(invoice.getUserName()).append("</p>\n");
            html.append("<p><strong>Email:</strong> ").append(invoice.getUserEmail()).append("</p>\n");
        }
        html.append("</div>\n");
        
        // Line items
        html.append("<table class='line-items'>\n");
        html.append("<thead>\n");
        html.append("<tr><th>Descripción</th><th>Cantidad</th><th>Precio Unitario</th><th>Total</th></tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");
        
        if (invoice.getLines() != null && !invoice.getLines().isEmpty()) {
            for (InvoiceDetailDTO.InvoiceLineDTO line : invoice.getLines()) {
                html.append("<tr>\n");
                html.append("<td>").append(line.getDescription()).append("</td>\n");
                html.append("<td>").append(line.getQuantity()).append("</td>\n");
                html.append("<td>$").append(line.getUnitPrice()).append("</td>\n");
                html.append("<td>$").append(line.getAmount()).append("</td>\n");
                html.append("</tr>\n");
            }
        } else {
            // Fallback line item
            html.append("<tr>\n");
            html.append("<td>Servicios Sigma Gym</td>\n");
            html.append("<td>1</td>\n");
            html.append("<td>$").append(invoice.getSubtotal()).append("</td>\n");
            html.append("<td>$").append(invoice.getSubtotal()).append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</tbody>\n");
        html.append("</table>\n");
        
        // Totals
        html.append("<div class='totals'>\n");
        html.append("<p>Subtotal: $").append(invoice.getSubtotal()).append("</p>\n");
        if (invoice.getTaxAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            html.append("<p>IVA (").append(invoice.getTaxRate()).append("%): $").append(invoice.getTaxAmount()).append("</p>\n");
        }
        html.append("<p class='total-row'>Total: $").append(invoice.getTotal()).append(" ").append(invoice.getCurrency()).append("</p>\n");
        html.append("</div>\n");
        
        html.append("</body>\n</html>");
        
        return html.toString();
    }
}
