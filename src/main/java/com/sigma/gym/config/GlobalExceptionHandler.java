package com.sigma.gym.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        
        // Verificar si es violación de unique constraint de email
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            error.put("error", "email_taken");
        } else {
            error.put("error", "data_integrity_violation");
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        
        // Si el mensaje contiene "email", asumimos que es email duplicado
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            error.put("error", "email_taken");
        } else {
            error.put("error", "invalid_input");
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap<>();
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> "Campo " + field.getField() + ": " + field.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación desconocido");
        
        error.put("error", "validation_failed");
        error.put("message", errorMessage);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // Muestra en consola
        Map<String, String> error = new HashMap<>();
        error.put("error", "internal_server_error");
        error.put("message", "Error inesperado: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
