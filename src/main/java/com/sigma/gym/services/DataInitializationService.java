package com.sigma.gym.services;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data initialization...");
        
        initializeRoles();
        initializeSeedUsers();
        
        log.info("Data initialization completed.");
    }

    private void initializeRoles() {
        log.info("Initializing roles...");
        
        // Create all roles if they don't exist
        for (RoleEntity.RoleName roleName : RoleEntity.RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                RoleEntity role = RoleEntity.builder()
                        .name(roleName)
                        .priority(roleName.getDefaultPriority())
                        .build();
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            }
        }
    }

    private void initializeSeedUsers() {
        log.info("Initializing seed users...");
        
        // Create Owner user if not exists
        String ownerEmail = "owner@sigmamgym.com";
        if (!userRepository.existsByEmail(ownerEmail)) {
            RoleEntity ownerRole = roleRepository.findByName(RoleEntity.RoleName.OWNER)
                    .orElseThrow(() -> new RuntimeException("Owner role not found"));

            UserEntity owner = new UserEntity();
            owner.setEmail(ownerEmail);
            owner.setPassword(passwordEncoder.encode("owner123"));
            owner.setFirstName("Admin");
            owner.setLastName("Owner");
            owner.setRoles(Set.of(ownerRole));
            owner.setIsActive(true);

            userRepository.save(owner);
            log.info("Created owner user with email: {}", ownerEmail);
        }

        // Create sample Trainer user if not exists
        String trainerEmail = "trainer@sigmamgym.com";
        if (!userRepository.existsByEmail(trainerEmail)) {
            RoleEntity trainerRole = roleRepository.findByName(RoleEntity.RoleName.TRAINER)
                    .orElseThrow(() -> new RuntimeException("Trainer role not found"));

            UserEntity trainer = new UserEntity();
            trainer.setEmail(trainerEmail);
            trainer.setPassword(passwordEncoder.encode("trainer123"));
            trainer.setFirstName("John");
            trainer.setLastName("Trainer");
            trainer.setRoles(Set.of(trainerRole));
            trainer.setIsActive(true);

            userRepository.save(trainer);
            log.info("Created trainer user with email: {}", trainerEmail);
        }

        // Create sample Member user if not exists
        String memberEmail = "member@sigmamgym.com";
        if (!userRepository.existsByEmail(memberEmail)) {
            RoleEntity memberRole = roleRepository.findByName(RoleEntity.RoleName.MEMBER)
                    .orElseThrow(() -> new RuntimeException("Member role not found"));

            UserEntity member = new UserEntity();
            member.setEmail(memberEmail);
            member.setPassword(passwordEncoder.encode("member123"));
            member.setFirstName("Jane");
            member.setLastName("Member");
            member.setRoles(Set.of(memberRole));
            member.setIsActive(true);

            userRepository.save(member);
            log.info("Created member user with email: {}", memberEmail);
        }
    }
}
