package com.sigma.gym.services.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.Role;
import com.sigma.gym.entity.User;
import com.sigma.gym.exceptions.UserException;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(RegisterRequest request) throws Exception {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserException("El usuario " + request.getUsername() + " ya existe");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserException("El email " + request.getEmail() + " ya está registrado.");
            }

            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new UserException("Rol no encontrado con ID: " + request.getRoleId()));

            User user = new User(
                    null,
                    request.getUsername(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    new ArrayList<>(), // orders
                    new ArrayList<>(), // progressHistory
                    new ArrayList<>(), // workoutPlans
                    new ArrayList<>(), // asistencia (si tenés)
                    new ArrayList<>()  // compras o lo que falte
            );

            return userRepository.save(user);

        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("[UserService.createUser] -> " + e.getMessage());
        }
    }

    public User getUserByUsername(String username) throws Exception {
        try {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserException("Usuario no encontrado"));
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("[UserService.getUserByUsername] -> " + e.getMessage());
        }
    }

    public Page<User> getUsers(PageRequest pageable) throws Exception {
        try {
            return userRepository.findAll(pageable);
        } catch (Exception e) {
            throw new Exception("[UserService.getAllUsers] -> " + e.getMessage());
        }
    }

    public User updateUser(User user) throws Exception {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("[UserService.updateUser] -> " + e.getMessage());
        }
    }

    public Optional<User> getUserById(Long userId) throws Exception {
        try {
            return userRepository.findById(userId);
        } catch (Exception e) {
            throw new Exception("[UserService.getUserById] -> " + e.getMessage());
        }
    }
}
