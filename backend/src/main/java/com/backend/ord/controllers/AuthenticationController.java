package com.backend.ord.controllers;

import com.backend.ord.api.requests.AuthenticationRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.api.responses.AuthenticationResponse;
import com.backend.ord.config.security.JwtProperties;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.services.AuthenticationService;
import com.backend.ord.services.UserSessionService;
import com.backend.ord.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserSessionService userSessionService;

    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException {
        // Handle registration
        AuthenticationResponse registerResult = authService.register(request);
        String token = registerResult.getToken();

        // Create cookie
        createCookie(token, response);

        // Create new user session
        createNewUserSession(token);

        // Return response
        return ResponseEntity.ok(registerResult);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException {
        // Handle login
        AuthenticationResponse loginResult = authService.login(request);
        String token = loginResult.getToken();

        // Create cookie
        createCookie(token, response);

        // Create new user session
        createNewUserSession(token);

        // Return response
        return ResponseEntity.ok(loginResult);
    }

    private void createCookie(String token, HttpServletResponse response) {
        String cookieName = jwtProperties.getAuthCookieName();
        CookieUtils.createCookie(cookieName, token, response);
    }

    private void createNewUserSession(String token) throws UserNotFoundException {
        userSessionService.openSessionFromJWT(token);
    }
}
