package com.backend.ord.services.impl;

import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.repositories.UserSessionRepository;
import com.backend.ord.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    public UserServiceImpl(UserRepository userRepository, UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public List<User> getAll() {
        Iterable<User> allUsers = userRepository.findAll();
        return StreamSupport.stream(allUsers.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByAuthToken(String authToken) {
        return userSessionRepository.findByToken(authToken)
                .map(UserSession::getUser);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
