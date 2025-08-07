// src/main/java/com/sigma/gym/repository/UsuarioRepository.java
package com.sigma.gym.repository;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity; // ✅ CORRECTO: Importar la ENTIDAD
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> { // ✅ CORRECTO: UserEntity
    
    Optional<UserEntity> findByUsername(String username);
    
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);


}
