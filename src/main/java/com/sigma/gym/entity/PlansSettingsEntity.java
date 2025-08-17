package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "plans_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlansSettingsEntity {
    
    @Id
    @NotNull
    @Builder.Default
    private Long id = 1L;
    
    @Column(name = "payment_cbu_cvu")
    private String paymentCbuCvu;
    
    @Column(name = "payment_alias")
    private String paymentAlias;
    
    @Column(name = "payment_whatsapp")
    private String paymentWhatsapp;
    
    @NotNull
    @Column(name = "payment_show_on_page", nullable = false)
    @Builder.Default
    private Boolean paymentShowOnPage = true;
    
    @Column(name = "payment_notes", columnDefinition = "TEXT")
    private String paymentNotes;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "discounts_json", columnDefinition = "JSON")
    private Map<String, Object> discountsJson;
}
