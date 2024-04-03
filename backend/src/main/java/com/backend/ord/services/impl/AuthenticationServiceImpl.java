package com.backend.ord.services.impl;

import com.backend.ord.api.requests.LoginRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.config.security.JwtFactory;
import com.backend.ord.config.security.JwtProperties;
import com.backend.ord.config.security.JwtService;
import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.UserRole;
import com.backend.ord.exceptions.ForbiddenException;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.services.AuthenticationService;
import com.backend.ord.services.UserSessionService;
import com.backend.ord.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtFactory jwtFactory;
    private final UserRepository userRepository;
    private final UserSessionService userSessionService;

    @Override
    public User register(
            RegisterRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException {
        // Save user to database
        User user = userRepository.save(
                // Create user object
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(UserRole.USER)
                        .build()

        );

        // Generate and assign a JWT token
        jwtFactory.createTokenForUser(user, response);

        return user;
    }


    @Override
    public User login(
            LoginRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        jwtFactory.createTokenForUser(user, response);

        return user;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ForbiddenException {
        String token = jwtService
                .getJWTFromRequest(request)
                .orElseThrow(() -> new ForbiddenException("No token found in request"));

        // Delete session from database
        userSessionService.deleteSessionByToken(token);

        // Delete JWT cookie
        CookieUtils.deleteCookie(jwtProperties.getAuthCookieName(), response);
    }
}
