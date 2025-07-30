// src/main/java/com/sigma/gym/controller/UserController.java
package com.sigma.gym.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sigma.gym.entity.UserEntity;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<UserEntity> getCurrentUser(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(user);
    }
}
