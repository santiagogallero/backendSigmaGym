package com.sigma.gym.services.auth;

import com.sigma.gym.controllers.auth.dtos.AuthenticationRequest;
import com.sigma.gym.controllers.auth.dtos.AuthenticationResponse;
import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.MembershipTypeEntity;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.UserException;
import com.sigma.gym.repository.MembershipTypeRepository;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.security.JwtService;

import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
public AuthenticationResponse register(RegisterRequest request) {
    System.out.println("🟢 Entró al método register de AuthenticationService");

 RoleEntity role = roleRepository.findByName("MEMBER")
        .orElseThrow(() -> new UserException("Rol MEMBER no existe en la base de datos"));

System.out.println("✅ Rol obtenido: " + role.getName() + ", id: " + role.getId());

    Set<RoleEntity> roles = new HashSet<>();
    roles.add(role);

    UserEntity user = new UserEntity();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setRoles(roles);

    user.setIsActive(true);
    user.setStartDate(LocalDate.now());
    // Aseguramos que todas las listas hijas sean null para evitar persistencia en cascada
    user.setRoutineProgressList(null);
    user.setWorkoutPlans(null);
    user.setWorkoutLogs(null);
    user.setProgressHistory(null);
    user.setAttendanceRecords(null);
    user.setCreatedExercises(null);
    user.setAssignedPlans(null);
    // membershipType debe ser null al registrar, se puede asignar más tarde
    user.setMembershipType(null);
// No inicializar listas de relaciones
    System.out.println("🧠 DEBUG antes de guardar usuario:");
    System.out.println(" - username: " + user.getUsername());
    user.getRoles().forEach(r ->
        System.out.println("   - rol: " + r.getName() + ", id: " + r.getId())
    );


    // Log de todas las listas hijas antes de guardar
    System.out.println("RoutineProgressList: " + user.getRoutineProgressList());
    System.out.println("WorkoutPlans: " + user.getWorkoutPlans());
    System.out.println("WorkoutLogs: " + user.getWorkoutLogs());
    System.out.println("ProgressHistory: " + user.getProgressHistory());
    System.out.println("AttendanceRecords: " + user.getAttendanceRecords());
    System.out.println("CreatedExercises: " + user.getCreatedExercises());
    System.out.println("AssignedPlans: " + user.getAssignedPlans());

    System.out.println("🧠 Guardando usuario con roles:");
    user.getRoles().forEach(r -> {
        System.out.println("   - Role: " + r.getName() + ", id = " + r.getId());
    });
    userRepository.save(user);

    // ⚠️ Asegurate de generar y devolver un token JWT válido
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