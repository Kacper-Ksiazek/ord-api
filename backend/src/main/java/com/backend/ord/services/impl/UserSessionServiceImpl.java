package com.backend.ord.services.impl;

import com.backend.ord.config.security.JwtService;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.repositories.UserSessionRepository;
import com.backend.ord.services.UserService;
import com.backend.ord.services.UserSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public UserSession save(UserSession userSession) {
        return userSessionRepository.save(userSession);
    }

    @Override
    public Optional<UserSession> findByToken(String token) {
        return userSessionRepository.findByToken(token);
    }

    @Override
    public Optional<UserSession> findByTokenAndUserId(String token, UUID userId) {
        return userSessionRepository.findByTokenAndUserId(token, userId);
    }

    @Override
    public void openSessionFromJWT(String token) throws UserNotFoundException{
        UUID userId = jwtService.extractUserId(token);
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        userSessionRepository.save(
            UserSession.builder()
                .token(token)
                .user(user)
                .build()
        );
    }
}
