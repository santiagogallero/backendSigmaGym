package com.sigma.gym.controllers.mock;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("OWNER", 1);
        createRoleIfNotExists("TRAINER", 2);
        createRoleIfNotExists("MEMBER", 3);
        RoleEntity memberRole = roleRepository.findByName("MEMBER").get();
        System.out.println("🔍 Role MEMBER obtenido desde base: ID = " + memberRole.getId());

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
}
