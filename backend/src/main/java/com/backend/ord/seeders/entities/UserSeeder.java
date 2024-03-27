package com.backend.ord.seeders.entities;

import com.backend.ord.domain.entities.User;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.seeders.factories.UserMockFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserSeeder implements SeederInterface<User>{
    private final UserRepository userRepository;

    @Override
    public User insertRow() {
        return userRepository.save(UserMockFactory.mockEntity());
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
