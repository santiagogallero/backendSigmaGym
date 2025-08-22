package com.sigma.gym.bootstrap;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.MembershipTypeEntity;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.repository.MembershipTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MembershipTypeRepository membershipTypeRepository;

    @Value("${sigmagym.seed.enabled:true}")
    private boolean seedEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Database seeding is disabled");
            return;
        }

        log.info("Starting database seeding...");

        try {
            // Seed Roles
            RoleEntity owner = seedRole(RoleEntity.RoleName.OWNER);
            RoleEntity trainer = seedRole(RoleEntity.RoleName.TRAINER);
            RoleEntity member = seedRole(RoleEntity.RoleName.MEMBER);

            // Seed MembershipType básico
            MembershipTypeEntity basicMembership = seedBasicMembershipType();

            // Seed Users
            seedOwnerUser(owner, basicMembership);
            seedTrainerUser(trainer, basicMembership);

            log.info("Database seeding completed successfully");
            
        } catch (Exception e) {
            log.error("Error during database seeding", e);
            throw e; // Re-throw para que falle el startup si hay problemas críticos
        }
    }

    private RoleEntity seedRole(RoleEntity.RoleName roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> {
                log.info("Creating role: {}", roleName);
                RoleEntity role = new RoleEntity();
                role.setName(roleName);
                role.setPriority(roleName.getDefaultPriority());
                return roleRepository.save(role);
            });
    }

    private MembershipTypeEntity seedBasicMembershipType() {
        return membershipTypeRepository.findByName("BASIC")
            .orElseGet(() -> {
                log.info("Creating basic membership type");
                MembershipTypeEntity basic = new MembershipTypeEntity();
                basic.setName("BASIC");
                basic.setAllowedDaysPerWeek(7); // Ilimitado por defecto
                // Configurar otros campos si existen en la entidad
                return membershipTypeRepository.save(basic);
            });
    }

    private void seedOwnerUser(RoleEntity ownerRole, MembershipTypeEntity basicMembership) {
        userRepository.findByEmail("owner@sigma.gym").orElseGet(() -> {
            log.info("Creating owner user: owner@sigma.gym");
            UserEntity owner = new UserEntity();
            owner.setEmail("owner@sigma.gym"); // Email actúa como username
            owner.setPassword(passwordEncoder.encode("Owner123!"));
            owner.setFirstName("Owner");
            owner.setLastName("Sigma");
            owner.setAge(35); // Campo obligatorio
            owner.setHealthCondition("Excelente"); // Campo obligatorio
            owner.setIsActive(true);
            owner.setStartDate(LocalDate.now());
            owner.setRoles(Set.of(ownerRole));
            if (basicMembership != null) {
                owner.setMembershipType(basicMembership);
            }
            return userRepository.save(owner);
        });
    }

    private void seedTrainerUser(RoleEntity trainerRole, MembershipTypeEntity basicMembership) {
        userRepository.findByEmail("trainer@sigma.gym").orElseGet(() -> {
            log.info("Creating trainer user: trainer@sigma.gym");
            UserEntity trainer = new UserEntity();
            trainer.setEmail("trainer@sigma.gym"); // Email actúa como username
            trainer.setPassword(passwordEncoder.encode("Trainer123!"));
            trainer.setFirstName("Trainer");
            trainer.setLastName("Sigma");
            trainer.setAge(30); // Campo obligatorio
            trainer.setHealthCondition("Muy buena"); // Campo obligatorio
            trainer.setIsActive(true);
            trainer.setStartDate(LocalDate.now());
            trainer.setRoles(Set.of(trainerRole));
            if (basicMembership != null) {
                trainer.setMembershipType(basicMembership);
            }
            return userRepository.save(trainer);
        });
    }
}
