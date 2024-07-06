package com.backend.ord.seeders.factories;

import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMockFactory extends AbstractFactory {
    private final PasswordEncoder passwordEncoder;

    public UserMockFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User mockEntity() {
        return User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.internet().password()))
                .role(UserRole.USER)
                .build();
    }

    public User mockEntityWithCredentials(String email, String password) {
        return User.builder()
                .name(faker.name().fullName())
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .build();
    }
}
