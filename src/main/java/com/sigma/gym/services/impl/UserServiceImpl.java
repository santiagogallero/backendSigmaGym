package com.sigma.gym.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

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
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserException("El usuario " + request.getUsername() + " ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("El email " + request.getEmail() + " ya está registrado.");
        }

        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new UserException("Rol no encontrado con ID: " + request.getRoleId()));

        MembershipTypeEntity membershipType = membershipTypeRepository.findByName(request.getMembershipType())
                .orElseThrow(() -> new RuntimeException("Tipo de membresía inválido"));

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role)) // ✅ ahora coincide el tipo

                .startDate(LocalDate.now())
                .membershipType(membershipType)
                .isActive(true)
                .workoutPlans(new ArrayList<>())
                .progressHistory(new ArrayList<>())
                .attendanceRecords(new ArrayList<>())
                .build();

        return userRepository.save(user);

    } catch (UserException e) {
        throw e;
    } catch (Exception e) {
        throw new Exception("[UserService.createUser] -> " + e.getMessage());
    }
}


    public UserEntity getUserByUsername(String username) throws Exception {
        try {
            return userRepository.findByUsername(username)
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
}
