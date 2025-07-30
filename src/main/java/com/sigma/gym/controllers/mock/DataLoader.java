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
        if (roleRepository.count() == 0) {
            roleRepository.save(new RoleEntity(null, "OWNER", 1));
            roleRepository.save(new RoleEntity(null, "TRAINER", 2));
            roleRepository.save(new RoleEntity(null, "MEMBER", 3));
            System.out.println("🚀 Roles cargados correctamente");
        }
    }
}
