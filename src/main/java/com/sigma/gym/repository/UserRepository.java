// src/main/java/com/sigma/gym/repository/UserRepository.java
package com.sigma.gym.repository;

import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    /**
     * Find user by email (used for authentication)
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all active users
     */
    List<UserEntity> findByIsActiveTrue();
    
    /**
     * Find users by role
     */
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    List<UserEntity> findByRoleAndActiveTrue(@Param("roleName") RoleEntity.RoleName roleName);
    
    /**
     * Find users by role (including inactive)
     */
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    List<UserEntity> findByRole(@Param("roleName") RoleEntity.RoleName roleName);
    
    /**
     * Count active users by role
     */
    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    long countByRoleAndActiveTrue(@Param("roleName") RoleEntity.RoleName roleName);
}
