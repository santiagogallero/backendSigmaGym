// src/main/java/com/sigma/gym/config/SecurityConfig.java
package com.sigma.gym.config;

import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.security.JwtAuthenticationFilter;
import com.sigma.gym.security.JwtService;
import com.sigma.gym.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }

   @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll() // registro/login sin autenticación
                    .requestMatchers("OPTIONS", "/**").permitAll() // permitir preflight OPTIONS
                    .requestMatchers("/api/plans", "/api/plans/**").permitAll() // public plans endpoints
                    .requestMatchers("/admin/**").hasRole("OWNER") // solo OWNER
                    .requestMatchers("/api/admin/**").hasRole("OWNER") // solo OWNER
                    .requestMatchers("/api/trainer/**").hasAnyRole("TRAINER", "OWNER") // trainer o owner
                    .requestMatchers("/api/member/**").hasAnyRole("MEMBER", "TRAINER", "OWNER") // cualquier rol válido
                    .anyRequest().authenticated() // todo lo demás requiere autenticación
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}

    

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir solo el origen específico del frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        
        // Permitir todos los métodos HTTP necesarios
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Permitir todos los headers necesarios
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept", 
            "X-Requested-With",
            "Cache-Control"
        ));
        
        // Permitir cookies y credenciales (necesario para JWT en headers)
        configuration.setAllowCredentials(true);
        
        // Exponer headers en las respuestas (útil para JWT y otros metadatos)
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Tiempo de cache para preflight requests (en segundos)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
