package com.backend.ord;

import com.backend.ord.domain.entities.LanguageProficiency;
import com.backend.ord.domain.entities.User;
import com.backend.ord.repositories.LanguageProficiencyRepository;
import com.backend.ord.repositories.UserRepository;
import com.backend.ord.seeders.entities.LanguageProficiencySeeder;
import com.backend.ord.seeders.entities.UserSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
    private final UserSeeder userSeeder;
    private final LanguageProficiencySeeder languageProficiencySeeder;

    public BackendApplication(UserSeeder userSeeder, LanguageProficiencySeeder languageProficiencySeeder) {
        this.userSeeder = userSeeder;
        this.languageProficiencySeeder = languageProficiencySeeder;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user = userSeeder.populate();
        LanguageProficiency languageProficiency = languageProficiencySeeder.populate(user);

        System.out.println("User: " + user);
        System.out.println("Language Proficiency: " + languageProficiency);
    }
}
