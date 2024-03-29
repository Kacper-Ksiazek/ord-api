package com.backend.ord.services.impl;

import com.backend.ord.api.requests.AuthenticationRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.api.responses.AuthenticationResponse;
import com.backend.ord.config.security.JwtService;
import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.UserRole;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // Create user object
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        // Save user to database
        userRepository.save(user);

        return getAuthenticationResponse(user);
    }


    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // TODO: Handle those exceptions
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return getAuthenticationResponse(user);
    }

    // Helper method to generate token
    private AuthenticationResponse getAuthenticationResponse(User user) {
        // Generate token
        String token = jwtService.generateToken(user);

        // Convert token to response object
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
