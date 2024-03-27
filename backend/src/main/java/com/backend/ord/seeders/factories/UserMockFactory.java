package com.backend.ord.seeders.factories;

import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.UserRole;

public class UserMockFactory extends AbstractFactory {
    public static User mockEntity() {
        return User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role(UserRole.USER)
                .build();
    }
}
