package com.backend.ord.seeders.factories;

import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMockFactory extends AbstractFactory {
    private final PasswordEncoder passwordEncoder;

    public User mockEntity() {
        return User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.internet().password()))
                .role(UserRole.USER)
                .build();
    }
}
