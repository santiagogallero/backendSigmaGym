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
        createRoleIfNotExists("OWNER", 1);
        createRoleIfNotExists("TRAINER", 2);
        createRoleIfNotExists("MEMBER", 3);

        RoleEntity ownerRole = roleRepository.findByName("OWNER").orElseThrow();
        RoleEntity trainerRole = roleRepository.findByName("TRAINER").orElseThrow();
        RoleEntity memberRole = roleRepository.findByName("MEMBER").orElseThrow();

        // Usuarios de prueba
        createUserIfNotExists("admin@sigma.com", "admin", "Admin", "Sigma", passwordEncoder.encode("1234"), Set.of(ownerRole, memberRole));
        createUserIfNotExists("trainer@sigma.com", "trainer", "John", "Trainer", passwordEncoder.encode("1234"), Set.of(trainerRole, memberRole));
        createUserIfNotExists("member@sigma.com", "member", "Jane", "Member", passwordEncoder.encode("1234"), Set.of(memberRole));
    }

    private void createRoleIfNotExists(String name, int priority) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(RoleEntity.builder()
                    .name(name)
                    .priority(priority)
                    .build());
            System.out.println("✅ Rol '" + name + "' creado");
        }
    }

    private void createUserIfNotExists(String email, String username, String firstName, String lastName, String password, Set<RoleEntity> roles) {
        if (userRepository.findByEmail(email).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            user.setRoles(roles);
            user.setIsActive(true);
            user.setStartDate(LocalDate.now());
            userRepository.save(user);
            System.out.println("👤 Usuario '" + username + "' creado");
        }
    }
}
