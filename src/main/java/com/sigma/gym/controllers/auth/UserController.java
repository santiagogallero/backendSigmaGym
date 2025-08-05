// src/main/java/com/sigma/gym/controller/UserController.java
package com.sigma.gym.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sigma.gym.controllers.user.UserDTO;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.mappers.UserMapper;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserEntity userEntity) {
        // Convertir la entidad a DTO usando tu mapper
        return ResponseEntity.ok(UserMapper.toDto(userEntity));
    }
}
