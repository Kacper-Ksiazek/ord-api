package com.backend.ord.controllers;

import com.backend.ord.api.requests.AuthenticationRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.config.security.JwtService;
import com.backend.ord.domain.dto.UserDTO;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.mappers.UserMapper;
import com.backend.ord.exceptions.ForbiddenException;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        try {
            authService.register(request, response);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        try {
            authService.login(request, response);

            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            authService.logout(request, response);
            return ResponseEntity.ok().build();
        }
        //
        catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @GetMapping("/current-user-info")
    public ResponseEntity<UserDTO> getCurrentlyLoggedInUser(
            HttpServletRequest request
    ) {
        Optional<User> authenticatedUser = jwtService.getAuthenticatedUser(request);

        return authenticatedUser
                .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());

    }
}
