// src/main/java/com/sigma/gym/controllers/auth/AdminUserController.java
package com.sigma.gym.controllers.auth;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/{userId}/promote-owner")
    @Transactional
    public ResponseEntity<?> promoteToOwner(@PathVariable Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RoleEntity owner = roleRepository.findByName("OWNER")
                .orElseThrow(() -> new RuntimeException("Role OWNER no existe"));

        if (user.getRoles() == null) user.setRoles(new HashSet<>());
        user.getRoles().add(owner);
        userRepository.save(user);

        return ResponseEntity.ok("Usuario " + user.getUsername() + " ahora es OWNER");
    }
}
