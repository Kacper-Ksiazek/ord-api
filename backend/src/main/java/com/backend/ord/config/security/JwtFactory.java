package com.backend.ord.config.security;

import com.backend.ord.api.responses.AuthenticationResponse;
import com.backend.ord.domain.entities.User;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.services.UserSessionService;
import com.backend.ord.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFactory {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    private final UserSessionService userSessionService;


    public AuthenticationResponse createTokenForUser(
            User user,
            HttpServletResponse response
    ) throws UserNotFoundException {
        // Generate token
        String token = jwtService.generateToken(
                getExtraClaims(user),
                user
        );

        // Create cookie
        createCookie(token, response);

        // Create new user session
        createNewUserSession(token);

        // Convert token to response object
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    // ### Helpers

    private Map<String, Object> getExtraClaims(User user) {
        String claimName = jwtProperties.getUserIdClaimName();

        return Map.of(
                claimName, user.getId()
        );
    }

    private void createCookie(String token, HttpServletResponse response) {
        String cookieName = jwtProperties.getAuthCookieName();
        CookieUtils.createCookie(cookieName, token, response);
    }

    private void createNewUserSession(String token) throws UserNotFoundException {
        userSessionService.openSessionFromJWT(token);
    }
}
