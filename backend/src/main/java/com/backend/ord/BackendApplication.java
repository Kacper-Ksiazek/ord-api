package com.backend.ord;

import com.backend.ord.domain.entities.User;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.seeders.entitiesSeeders.UserSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
    UserRepository userRepository;

    public BackendApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User createdUser = userRepository.save(UserSeeder.createUser());

        System.out.println("Created user: " + createdUser.toString());
    }
}
