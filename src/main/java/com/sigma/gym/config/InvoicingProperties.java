package com.sigma.gym.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gym.invoicing")
public class InvoicingProperties {
    
    private String timezone = "America/Argentina/Buenos_Aires";
    private String defaultInvoiceType = "RECEIPT";
    private Tax tax = new Tax();
    private Numbering numbering = new Numbering();
    private Storage storage = new Storage();
    private Email email = new Email();
    
    @Data
    public static class Tax {
        private boolean enabled = true;
        private double rate = 21.0;
    }
    
    @Data
    public static class Numbering {
        private String pos = "0001";
        private String series = "A";
    }
    
    @Data
    public static class Storage {
        private String backend = "FS";
        private String basePath = "/var/app/invoices";
    }
    
    @Data
    public static class Email {
        private String from = "no-reply@sigmagym.app";
        private String subjectTemplate = "Your receipt ${invoiceNumber}";
    }
}
