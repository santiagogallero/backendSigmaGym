package com.sigma.gym.services.auth;

import com.sigma.gym.controllers.auth.dtos.AuthenticationRequest;
import com.sigma.gym.controllers.auth.dtos.AuthenticationResponse;
import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.UserException;
import com.sigma.gym.repository.MembershipTypeRepository;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.security.JwtService;

import jakarta.security.auth.message.AuthException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    // Inyectar las dependencias necesarias
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MembershipTypeRepository membershipTypeRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

@Transactional
public AuthenticationResponse register(RegisterRequest request) {


    RoleEntity role = roleRepository.findByName("MEMBER")
        .orElseThrow(() -> new UserException("Rol MEMBER no existe en la base de datos"));
    System.out.println("✅ Rol obtenido: " + role.getName() + ", id: " + role.getId());

    UserEntity user = new UserEntity();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setIsActive(true);
    user.setStartDate(LocalDate.now());

    // ⚠️ Paso 1: guardar SIN roles para obtener ID
    user.setRoles(new HashSet<>());       // ← vacío por ahora
    // (mantener las colecciones @OneToMany en null como tenías)
    user.setRoutineProgressList(null);
    user.setWorkoutPlans(null);
    user.setWorkoutLogs(null);
    user.setProgressHistory(null);
    user.setAttendanceRecords(null);
    user.setCreatedExercises(null);
    user.setAssignedPlans(null);
    user.setMembershipType(null);

    user = userRepository.save(user);     // ← ya tiene ID

    // ✅ Paso 2: ahora sí, agregar el rol y persistir la tabla puente user_roles
    user.getRoles().add(role);
    user = userRepository.save(user);

    // Token
    String jwt = jwtService.generateToken(user);

    return AuthenticationResponse.builder()
        .accessToken(jwt)
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(role.getName())
        .build();
}



    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            String token = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
        .accessToken(token)
        .email(user.getEmail())
        .username(user.getUsername())
        .roles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet()))
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();

        } catch (AuthenticationException e) {
            throw new AuthException("Invalid email or password");
        }
    }
    public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
}