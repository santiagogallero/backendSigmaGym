// src/main/java/com/sigma/gym/controller/AuthController.java
package com.sigma.gym.controllers.auth;

import com.sigma.gym.controllers.auth.dtos.AuthenticationRequest;
import com.sigma.gym.controllers.auth.dtos.AuthenticationResponse;
import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.exceptions.UserException;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.auth.AuthenticationService;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;



@PostMapping("/register")
public ResponseEntity<ResponseData<?>> register(@Valid @RequestBody RegisterRequest request) {
    System.out.println("➡️ Request a: /auth/register");
    try {
        AuthenticationResponse authResponse = authService.register(request);
            if(authResponse.getAccessToken() != null) 
                return ResponseEntity.status(HttpStatus.OK).body(ResponseData.success(authResponse));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.error("No se pudo registrar el usuario"));

        } catch (UserException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.error(error.getMessage()));
        } catch (Exception error) {
    System.out.println("[AuthenticationController.register] ❌ EXCEPCIÓN DETECTADA:");
    error.printStackTrace(); // Esto imprimirá TODO el stacktrace real

    // Para que el mensaje en Postman sea más útil:
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ResponseData.error("Error interno: " + error.getMessage()));
}

    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseData<?>> authenticate(
        @RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.success(authService.authenticate(request)));
        } catch (UserException | AuthException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.error(error.getMessage()));
        } catch (Exception error) {
            System.out.printf("[AuthenticationController.authenticate] -> %s", error.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error("Usuario o contraseña inválido."));
        }
    }
}