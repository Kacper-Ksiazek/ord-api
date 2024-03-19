package com.backend.ord.seeders.mocks;

import com.backend.ord.domain.entities.User;
import org.springframework.stereotype.Component;

public class UserMockFactory extends AbstractFactory {
    public static User mockEntity() {
        return User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .build();
    }
}
