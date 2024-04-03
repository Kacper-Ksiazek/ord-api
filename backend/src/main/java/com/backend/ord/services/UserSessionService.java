package com.backend.ord.services;

import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionService {
    UserSession save (UserSession userSession);

    Optional<UserSession> findByToken(String token);

    Optional<UserSession> findByTokenAndUserId(String token, UUID userId);

    void openSessionFromJWT(String token) throws UserNotFoundException;

    void deleteSessionByToken(String authToken);
}
