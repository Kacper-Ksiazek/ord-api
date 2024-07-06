package com.backend.ord.config.security;

import com.backend.ord.config.properties.JwtProperties;
import com.backend.ord.domain.entities.User;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.services.UserSessionService;
import com.backend.ord.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtFactory {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    private final UserSessionService userSessionService;

    public JwtFactory(JwtService jwtService, JwtProperties jwtProperties, UserSessionService userSessionService) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userSessionService = userSessionService;
    }


    public String createTokenForUser(
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

        // Return generated JWT token
        return token;
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
