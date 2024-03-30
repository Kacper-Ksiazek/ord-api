package com.backend.ord.services.impl;

import com.backend.ord.api.requests.AuthenticationRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.api.responses.AuthenticationResponse;
import com.backend.ord.config.security.JwtFactory;
import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.UserRole;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final JwtFactory jwtFactory;

    @Override
    public AuthenticationResponse register(
            RegisterRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException {
        // Create user object
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        // Save user to database
        userRepository.save(user);

        return jwtFactory.createTokenForUser(user, response);
    }


    @Override
    public AuthenticationResponse login(
            AuthenticationRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return jwtFactory.createTokenForUser(user, response);
    }
}
