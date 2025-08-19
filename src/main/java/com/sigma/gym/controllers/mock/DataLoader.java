package com.sigma.gym.controllers.mock;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Roles
        createRoleIfNotExists(RoleEntity.RoleName.OWNER, 1);
        createRoleIfNotExists(RoleEntity.RoleName.TRAINER, 2);
        createRoleIfNotExists(RoleEntity.RoleName.MEMBER, 3);

        RoleEntity ownerRole = roleRepository.findByName(RoleEntity.RoleName.OWNER).orElseThrow();
        RoleEntity trainerRole = roleRepository.findByName(RoleEntity.RoleName.TRAINER).orElseThrow();
        RoleEntity memberRole = roleRepository.findByName(RoleEntity.RoleName.MEMBER).orElseThrow();

        // Usuarios de prueba
        createUserIfNotExists("admin@sigma.com", "Admin", "Sigma", 30, "Excelente condición física", passwordEncoder.encode("123456"), Set.of(ownerRole, memberRole));
        createUserIfNotExists("trainer@sigma.com", "John", "Trainer", 25, "Muy buen estado físico", passwordEncoder.encode("123456"), Set.of(trainerRole, memberRole));
        createUserIfNotExists("member@sigma.com", "Jane", "Member", 22, "Buena condición física", passwordEncoder.encode("123456"), Set.of(memberRole));
    }

    private void createRoleIfNotExists(RoleEntity.RoleName name, int priority) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(RoleEntity.builder()
                    .name(name)
                    .priority(priority)
                    .build());
            System.out.println("✅ Rol '" + name + "' creado");
        }
    }

    private void createUserIfNotExists(String email, String firstName, String lastName, Integer age, String healthCondition, String password, Set<RoleEntity> roles) {
        if (userRepository.findByEmail(email).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            // Remove setUsername call
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setHealthCondition(healthCondition);
            user.setPassword(password);
            user.setRoles(roles);
            user.setIsActive(true);
            user.setStartDate(LocalDate.now());
            userRepository.save(user);
            System.out.println("👤 Usuario '" + email + "' creado");
        }
    }
}
