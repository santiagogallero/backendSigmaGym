// src/main/java/com/sigma/gym/repository/UsuarioRepository.java
package com.sigma.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sigma.gym.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username); // 👈 Este está perfecto

    Boolean existsByUsername(String username); // 👈 Correcto para validar existencia previa

    Boolean existsByEmail(String email); // 👈 También bien

    Optional<User> findByEmail(String email); // 👈 Acá está el cambio necesario
}
