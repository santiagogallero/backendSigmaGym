package com.sigma.gym.bootstrap;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Ensure roles are created before users
public class RoleBootstrap implements CommandLineRunner {
    
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        log.info("🔧 Starting role bootstrap...");
        
        ensureRole(RoleEntity.RoleName.OWNER);
        ensureRole(RoleEntity.RoleName.TRAINER);
        ensureRole(RoleEntity.RoleName.MEMBER);
        
        log.info("✅ Role bootstrap completed");
    }
    
    private void ensureRole(RoleEntity.RoleName roleName) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            log.info("Creating role: {}", roleName);
            RoleEntity role = RoleEntity.builder()
                    .name(roleName)
                    .priority(roleName.getDefaultPriority())
                    .build();
            return roleRepository.save(role);
        });
    }
}
