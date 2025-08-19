package com.sigma.gym.controllers.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.services.UserService;
import com.sigma.gym.exceptions.UserException;
import com.sigma.gym.mappers.UserMapper;
import com.sigma.gym.response.ResponseData; // Asegurate que esté bien el import

@RestController
@RequestMapping("/users")
public class AuthUserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Page<UserEntity>> getUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws Exception {
        if (page == null || size == null)
            return ResponseEntity.ok(userService.getUsers(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(userService.getUsers(PageRequest.of(page, size)));
    }

    @GetMapping("/data")
    public ResponseEntity<ResponseData<?>> getUserData(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserEntity authUser = userService.getUserByUsername(userDetails.getUsername());
            UserDTO userDTO = UserMapper.toDto(authUser); // Asegurate de tener este método o usar un Mapper
            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.ok(userDTO));
        } catch (UserException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseData.error(error.getMessage()));
        } catch (Exception error) {
            System.out.printf("[UserController.getUserData] -> %s", error.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.error("No se pudo encontrar el usuario"));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseData<?>> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody UserDTO userDTO) {
        try {
            UserEntity authUser = userService.getUserByUsername(userDetails.getUsername());
            UserEntity user = UserMapper.toEntity(userDTO); // Asegurate de tener este método o usar un Mapper
            authUser.updateData(user);

            String password = user.getPassword();
            if (!"null".equals(password)) {
                authUser.setPassword(passwordEncoder.encode(password));
            }

            UserEntity updatedUser = userService.updateUser(authUser);
          UserDTO updatedUserDTO = UserMapper.toDto(updatedUser);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.ok(updatedUserDTO));

        } catch (UserException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseData.error(error.getMessage()));
        } catch (Exception error) {
            System.out.printf("[UserController.updateUser] -> %s", error.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.error("No se pudo actualizar el usuario"));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long userId) throws Exception {
        Optional<UserEntity> result = userService.getUserById(userId);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
