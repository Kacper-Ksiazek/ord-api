package com.backend.ord.services;

import com.backend.ord.domain.entities.User;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
public interface UserService {
    List<User> getAll();

    Optional<User> findById(UUID id);

    User save(User user);

    Optional<User> findUserByAuthToken(String authToken);
}
