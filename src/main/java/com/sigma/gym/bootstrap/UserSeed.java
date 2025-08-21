package com.sigma.gym.bootstrap;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2) // Run after role bootstrap
public class UserSeed implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.owner.email:owner@sigmagym.local}")
    private String ownerEmail;
    
    @Value("${app.seed.owner.username:owner}")
    private String ownerUsername;
    
    @Value("${app.seed.owner.password:Owner123!}")
    private String ownerPassword;

    @Value("${app.seed.trainer.email:trainer@sigmagym.local}")
    private String trainerEmail;
    
    @Value("${app.seed.trainer.username:trainer}")
    private String trainerUsername;
    
    @Value("${app.seed.trainer.password:Trainer123!}")
    private String trainerPassword;

    @Override
    public void run(String... args) {
        log.info("🌱 Starting user seed...");
        
        seedOne(ownerUsername, ownerEmail, ownerPassword, RoleEntity.RoleName.OWNER);
        seedOne(trainerUsername, trainerEmail, trainerPassword, RoleEntity.RoleName.TRAINER);
        
        log.info("✅ User seed completed");
    }

    private void seedOne(String username, String email, String rawPassword, RoleEntity.RoleName roleName) {
        // Check if user already exists by email
        if (userRepository.existsByEmail(email)) {
            log.debug("User with email {} already exists, skipping", email);
            return;
        }

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " not found. Run RoleBootstrap first."));

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(roleName.name().toLowerCase());
        user.setLastName("seed");
        user.setAge(30);
        user.setHealthCondition("Excellent");
        user.setRoles(Set.of(role));
        user.setIsActive(true);
        user.setStartDate(LocalDate.now());

        userRepository.save(user);
        log.info("Created {} user: {}", roleName, email);
    }
}
