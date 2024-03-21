package com.backend.ord.seeders;

import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.LanguageName;
import com.backend.ord.seeders.entities.LanguageProficiencySeeder;
import com.backend.ord.seeders.entities.UserSeeder;
import com.backend.ord.seeders.factories.LanguageProficiencyFactory;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Profile("local")
@AllArgsConstructor
@Order(Integer.MIN_VALUE)
public class DatabaseSeeder implements ApplicationRunner {
    private final UserSeeder userSeeder;
    private final LanguageProficiencySeeder languageProficiencySeeder;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(ANSI_YELLOW + "Seeding database initialized..." + ANSI_RESET);

        System.out.println("Deleting all data from database...");
        userSeeder.deleteAll();
        languageProficiencySeeder.deleteAll();

        System.out.println("Inserting data into database...");
        IntStream.range(0, 10).forEach(i -> {
            User createdUser = userSeeder.insertRow();
            List<LanguageName> languages = LanguageProficiencyFactory.mockUniqueLanguages(3);

            languages.forEach(language -> {
                languageProficiencySeeder.insertRow(createdUser, language);
            });
        });
    }
}
