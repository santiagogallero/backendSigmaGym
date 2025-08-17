package com.sigma.gym.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sigma.gym.entity.RoleEntity;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    
    /**
     * Find role by name
     */
    Optional<RoleEntity> findByName(RoleEntity.RoleName name);
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(RoleEntity.RoleName name);
}
