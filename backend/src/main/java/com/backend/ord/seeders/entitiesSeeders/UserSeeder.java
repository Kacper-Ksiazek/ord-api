package com.backend.ord.seeders.entitiesSeeders;

import com.backend.ord.domain.entities.User;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;

@Component
public class UserSeeder {
    private UserSeeder() {
    }

    private final static Faker faker = Faker.instance();

    public static User createUser() {
        return User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .build();
    }

    public static User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    public static User createUser(String email) {
        return User.builder()
                .name(faker.name().fullName())
                .email(email)
                .build();
    }

}
