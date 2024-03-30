package com.backend.ord.config.security;

import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.exceptions.JWTTokenIsExpired;
import com.backend.ord.exceptions.NoCorrespondingUserSessionException;
import com.backend.ord.services.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtTokenValidator {
    private final JwtService jwtService;
    private final UserSessionService userSessionService;

    public boolean validate(
            String jwtToken,
            UserDetails userDetails
    ) throws
            JWTTokenIsExpired,
            NoCorrespondingUserSessionException {
        final String username = jwtService.extractUsername(jwtToken);

        // Check if the token has a corresponding session
        validateCorrespondingSession(jwtToken);

        // Check if the token is expired
        validateTokenExpiration(jwtToken);

        // Check if the token's username matches the user's username
        return userDetails.getUsername().equals(username);
    }

    public void validateCorrespondingSession(String jwtToken) throws NoCorrespondingUserSessionException {
        if (isTokenMissingCorrespondingSession(jwtToken)) {
            throw new NoCorrespondingUserSessionException("No corresponding session found for the user");
        }
    }

    public void validateTokenExpiration(String jwtToken) throws JWTTokenIsExpired {
        if (isTokenExpired(jwtToken)) {
            throw new JWTTokenIsExpired("The JWT token has expired");
        }
    }

    private boolean isTokenMissingCorrespondingSession(String jwtToken) {
        UUID userId = jwtService.extractUserId(jwtToken);
        Optional<UserSession> correspondingSession = userSessionService.findByTokenAndUserId(jwtToken, userId);

        return correspondingSession.isEmpty();
    }

    private boolean isTokenExpired(String jwtToken) {
        Date expirationDate = jwtService.extractExpiration(jwtToken);
        assert expirationDate != null;

        return expirationDate.before(new Date());
    }
}
