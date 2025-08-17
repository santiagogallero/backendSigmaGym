package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plan_price_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanPriceTierEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Integer timesPerWeek;
    
    @NotNull
    @PositiveOrZero(message = "Price must be zero or positive")
    @Column(nullable = false)
    private BigDecimal priceARS;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanEntity plan;
}
