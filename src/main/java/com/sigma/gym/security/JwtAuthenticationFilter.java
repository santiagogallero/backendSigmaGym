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
    // Permite Base64URL (A-Z a-z 0-9 - _ .) y tolera padding '=' opcional
    private static final Pattern BEARER_TOKEN_PATTERN = Pattern.compile("^Bearer\\s+([A-Za-z0-9\\-\\._=]+)$");
    
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
        String uri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        boolean hasAuth = authHeader != null && !authHeader.trim().isEmpty();
        logger.debug("shouldNotFilter? method={}, path={}, uri={}, hasAuthHeader={}", method, path, uri, hasAuth);

        // Skip filter for OPTIONS requests
        if ("OPTIONS".equals(method)) {
            logger.trace("Skipping filter: OPTIONS request");
            return true;
        }

        // Skip filter for public routes
        if (path.startsWith("/auth/") ||
            path.startsWith("/actuator/") ||
            path.startsWith("/docs/") ||
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/v3/api-docs/") ||
            path.startsWith("/static/")) {
            logger.trace("Skipping filter: public route {}", path);
            return true;
        }

        // Skip filter for requests without Authorization header
        if (!hasAuth) {
            logger.trace("Skipping filter: missing Authorization header for {}", uri);
            return true;
        }

        logger.trace("Filter WILL run for {}", uri);
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1) Log path y header Authorization
        logger.debug("Processing request: {}", request.getRequestURI());
        
        String authHeader = request.getHeader("Authorization");
        boolean hasAuthHeader = authHeader != null && !authHeader.trim().isEmpty();
        int headerLen = hasAuthHeader ? authHeader.length() : 0;
        logger.debug("Auth hdr present? {}, len={}", hasAuthHeader, headerLen);
        
        // Use regex to extract token from Bearer header
        Matcher matcher = BEARER_TOKEN_PATTERN.matcher(authHeader);
        if (!matcher.matches()) {
            // 6) No Bearer token
            logger.debug("No Bearer token on {}", request.getRequestURI());
            logger.trace("Authorization header does not match Bearer token pattern");
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = matcher.group(1);
        
        // Continue without logging if token is null or empty
        if (token == null || token.trim().isEmpty()) {
            logger.debug("No Bearer token on {}", request.getRequestURI());
            logger.trace("Token is null or empty");
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2) Log token parts
        String[] tokenParts = token.split("\\.");
        logger.debug("tokenParts={}", tokenParts.length);
        
        String email;
        try {
            email = jwtService.extractUsername(token);
            logger.debug("subject/email={}", email);
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
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
            
            // 3) Log loaded user details
            logger.debug("loaded userDetails username={}", user.getEmail());
            
            // 4) Log validation process
            logger.debug("validating token...");
            boolean isValid = jwtService.isTokenValid(token, user);
            boolean isExpired = jwtService.isTokenExpired(token);
            logger.debug("isValid={}, expired={}", isValid, isExpired);
            
            if (isValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 5) Log SecurityContext setting
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("Authentication set in SecurityContext for {}", user.getEmail());
            } else {
                logger.trace("Invalid token for user: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}
