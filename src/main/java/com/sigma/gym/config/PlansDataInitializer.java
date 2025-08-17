package com.sigma.gym.config;

import com.sigma.gym.entity.PlanEntity;
import com.sigma.gym.entity.PlanPriceTierEntity;
import com.sigma.gym.entity.PlansSettingsEntity;
import com.sigma.gym.repository.PlanPriceTierRepository;
import com.sigma.gym.repository.PlanRepository;
import com.sigma.gym.repository.PlansSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(3) // Run after user and role initialization
@RequiredArgsConstructor
public class PlansDataInitializer implements CommandLineRunner {
    
    private final PlanRepository planRepository;
    private final PlanPriceTierRepository priceTierRepository;
    private final PlansSettingsRepository settingsRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializePlansSettings();
        initializePlans();
    }
    
    private void initializePlansSettings() {
        if (settingsRepository.count() == 0) {
            PlansSettingsEntity settings = new PlansSettingsEntity();
            
            // Payment information
            settings.setPaymentCbuCvu("0000003100039728471853");
            settings.setPaymentAlias("sigma.gym.fitness");
            settings.setPaymentWhatsapp("+54 9 11 1234-5678");
            settings.setPaymentShowOnPage(true);
            settings.setPaymentNotes("Transferencias bancarias, CVU o alias. Contactar por WhatsApp para confirmar pago.");
            
            // Discount rules
            Map<String, Object> discountRules = new HashMap<>();
            discountRules.put("cashDiscountPercent", 5.0);
            discountRules.put("earlyPaymentDay", 5);
            discountRules.put("description", "5% descuento por pago en efectivo antes del día 5 del mes");
            discountRules.put("contactEmail", "info@sigmagym.com");
            settings.setDiscountsJson(discountRules);
            
            settingsRepository.save(settings);
            System.out.println("✅ Plans settings initialized successfully");
        }
    }
    
    private void initializePlans() {
        if (planRepository.count() == 0) {
            // 1. Plan SemiPersonalizado with price tiers
            PlanEntity semiPersonalizado = new PlanEntity();
            semiPersonalizado.setName("SemiPersonalizado");
            semiPersonalizado.setCategory("SemiPersonalizado");
            semiPersonalizado.setDescription("Entrenamiento semi personalizado con seguimiento profesional");
            semiPersonalizado.setVisible(true);
            semiPersonalizado.setSortOrder(1);
            
            Map<String, Object> metadata1 = new HashMap<>();
            metadata1.put("features", Arrays.asList(
                "Rutina personalizada",
                "Seguimiento profesional",
                "Evaluaciones periódicas",
                "Asesoramiento nutricional básico"
            ));
            metadata1.put("duration", "Mensual");
            semiPersonalizado.setMetadata(metadata1);
            
            PlanEntity savedSemiPersonalizado = planRepository.save(semiPersonalizado);
            
            // Add price tiers for SemiPersonalizado
            PlanPriceTierEntity tier1 = new PlanPriceTierEntity();
            tier1.setPlan(savedSemiPersonalizado);
            tier1.setTimesPerWeek(1);
            tier1.setPriceARS(new BigDecimal("25000"));
            
            PlanPriceTierEntity tier2 = new PlanPriceTierEntity();
            tier2.setPlan(savedSemiPersonalizado);
            tier2.setTimesPerWeek(2);
            tier2.setPriceARS(new BigDecimal("35000"));
            
            PlanPriceTierEntity tier3 = new PlanPriceTierEntity();
            tier3.setPlan(savedSemiPersonalizado);
            tier3.setTimesPerWeek(3);
            tier3.setPriceARS(new BigDecimal("45000"));
            
            PlanPriceTierEntity tier4 = new PlanPriceTierEntity();
            tier4.setPlan(savedSemiPersonalizado);
            tier4.setTimesPerWeek(4);
            tier4.setPriceARS(new BigDecimal("55000"));
            
            PlanPriceTierEntity tier5 = new PlanPriceTierEntity();
            tier5.setPlan(savedSemiPersonalizado);
            tier5.setTimesPerWeek(5);
            tier5.setPriceARS(new BigDecimal("65000"));
            
            priceTierRepository.saveAll(Arrays.asList(tier1, tier2, tier3, tier4, tier5));
            
            // 2. Plan Kids
            PlanEntity kids = new PlanEntity();
            kids.setName("Kids");
            kids.setCategory("Kids");
            kids.setDescription("Plan especial para niños de 6 a 12 años con actividades recreativas");
            kids.setVisible(true);
            kids.setSortOrder(2);
            kids.setFlatPriceARS(new BigDecimal("28000"));
            
            Map<String, Object> metadata2 = new HashMap<>();
            metadata2.put("ageRange", "6-12 años");
            metadata2.put("features", Arrays.asList(
                "Actividades recreativas",
                "Desarrollo motor",
                "Juegos grupales",
                "Instructor especializado en niños"
            ));
            metadata2.put("duration", "Mensual");
            metadata2.put("schedule", "Lunes, Miércoles y Viernes de 16:00 a 17:00");
            kids.setMetadata(metadata2);
            
            planRepository.save(kids);
            
            // 3. Plan Online (visible sin precio)
            PlanEntity online = new PlanEntity();
            online.setName("Online");
            online.setCategory("Online");
            online.setDescription("Entrenamiento virtual desde la comodidad de tu hogar");
            online.setVisible(true);
            online.setSortOrder(3);
            // No flat price, so users need to contact for pricing
            
            Map<String, Object> metadata3 = new HashMap<>();
            metadata3.put("features", Arrays.asList(
                "Clases en vivo por Zoom",
                "Rutinas personalizadas",
                "Seguimiento virtual",
                "Biblioteca de ejercicios",
                "Horarios flexibles"
            ));
            metadata3.put("duration", "Mensual");
            metadata3.put("contactRequired", true);
            metadata3.put("platforms", Arrays.asList("Zoom", "Google Meet", "WhatsApp"));
            online.setMetadata(metadata3);
            
            planRepository.save(online);
            
            System.out.println("✅ Base plans initialized successfully:");
            System.out.println("   - SemiPersonalizado (1-5 times/week: $25k-$65k ARS)");
            System.out.println("   - Kids ($28k ARS)"); 
            System.out.println("   - Online (contact for pricing)");
        }
    }
}
