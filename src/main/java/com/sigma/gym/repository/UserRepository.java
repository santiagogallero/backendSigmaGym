// src/main/java/com/sigma/gym/repository/UsuarioRepository.java
package com.sigma.gym.repository;

import com.sigma.gym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}