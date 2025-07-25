package com.sigma.gym.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sigma.gym.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);


    

    
}
