package com.backend.ord.seeders.entities;

import com.backend.ord.domain.entities.User;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.seeders.mocks.UserMockFactory;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements SeederInterface<User>{
    private final UserRepository userRepository;

    public UserSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User populate() {
        return userRepository.save(UserMockFactory.mockEntity());
    }

}
