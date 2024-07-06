package com.backend.ord.seeders.entities;

import com.backend.ord.domain.entities.User;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.seeders.factories.UserMockFactory;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements SeederInterface<User> {
    private final UserRepository userRepository;
    private final UserMockFactory userMockFactory;

    public UserSeeder(UserRepository userRepository, UserMockFactory userMockFactory) {
        this.userRepository = userRepository;
        this.userMockFactory = userMockFactory;
    }

    @Override
    public User insertRow() {
        return userRepository.save(userMockFactory.mockEntity());
    }

    public User insertRowWithCredentials(String email, String password) {
        return userRepository.save(userMockFactory.mockEntityWithCredentials(email, password));
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
