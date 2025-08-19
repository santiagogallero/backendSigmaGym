package com.sigma.gym.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.MembershipTypeEntity;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.UserException;
import com.sigma.gym.repository.MembershipTypeRepository;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.UserService;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MembershipTypeRepository membershipTypeRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

 @Transactional
public UserEntity createUser(RegisterRequest request) throws Exception {
    try {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("El email " + request.getEmail() + " ya está registrado.");
        }

        RoleEntity role = roleRepository.findByName(RoleEntity.RoleName.MEMBER)
        .orElseThrow(() -> new UserException("No se encontró el rol por defecto: MEMBER"));

        UserEntity user = new UserEntity();
        // Remove setUsername call
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));
        user.setStartDate(LocalDate.now());

        user.setIsActive(true);
        // No inicializar listas hijas aquí


        return userRepository.save(user);

    } catch (UserException e) {
        throw e;
    } catch (Exception e) {
        throw new Exception("[UserService.createUser] -> " + e.getMessage());
    }
}


    public UserEntity getUserByUsername(String username) throws Exception {
        try {
            return userRepository.findByEmail(username) // Use email instead of username
                    .orElseThrow(() -> new UserException("Usuario no encontrado"));
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("[UserService.getUserByUsername] -> " + e.getMessage());
        }
    }

    public Page<UserEntity> getUsers(PageRequest pageable) throws Exception {
        try {
            return userRepository.findAll(pageable);
        } catch (Exception e) {
            throw new Exception("[UserService.getAllUsers] -> " + e.getMessage());
        }
    }

    public UserEntity updateUser(UserEntity user) throws Exception {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("[UserService.updateUser] -> " + e.getMessage());
        }
    }

    public Optional<UserEntity> getUserById(Long userId) throws Exception {
        try {
            return userRepository.findById(userId);
        } catch (Exception e) {
            throw new Exception("[UserService.getUserById] -> " + e.getMessage());
        }
    }

    /**
     * Genera un username único a partir del email
     * Usa la parte local del email + UUID corto para garantizar unicidad
     */
    private String generateUsernameFromEmail(String email) {
        String localPart = email.substring(0, email.indexOf('@'));
        // Generar un sufijo único de 8 caracteres
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return localPart + "_" + uniqueSuffix;
    }
    
    @Override
    public UserEntity findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException("No se encontró un usuario con el email: " + email));
    }
}
