package com.sigma.gym.security;

import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final Pattern BEARER_TOKEN_PATTERN = Pattern.compile("^Bearer\\s+([A-Za-z0-9\\-\\._]+)$");
    
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String path = request.getServletPath();
        String authHeader = request.getHeader("Authorization");
        
        // Skip filter for OPTIONS requests
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        // Skip filter for public routes
        if (path.startsWith("/auth/") ||
            path.startsWith("/actuator/") ||
            path.startsWith("/docs/") ||
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/v3/api-docs/") ||
            path.startsWith("/static/")) {
            return true;
        }
        
        // Skip filter for requests without Authorization header
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return true;
        }
        
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        // Use regex to extract token from Bearer header
        Matcher matcher = BEARER_TOKEN_PATTERN.matcher(authHeader);
        if (!matcher.matches()) {
            logger.trace("Authorization header does not match Bearer token pattern");
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = matcher.group(1);
        
        // Continue without logging if token is null or empty
        if (token == null || token.trim().isEmpty()) {
            logger.trace("Token is null or empty");
            filterChain.doFilter(request, response);
            return;
        }
        
        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            logger.trace("Failed to extract username from token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                logger.trace("User not found for email: {}", email);
                filterChain.doFilter(request, response);
                return;
            }
            
            if (jwtService.isTokenValid(token, user)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("User {} authenticated successfully", email);
            } else {
                logger.trace("Invalid token for user: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}
