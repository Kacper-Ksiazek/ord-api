package com.backend.ord.seeders;

import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.seeders.entities.LanguageProficiencySeeder;
import com.backend.ord.seeders.entities.UserSeeder;
import com.backend.ord.seeders.factories.LanguageProficiencyFactory;
import com.backend.ord.utils.Console;
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
    private final LanguageProficiencyFactory languageProficiencyFactory;

    @Override
    public void run(ApplicationArguments args) {
        // Print a message to the console
        Console.printCyan("Seeding database:\n");

        // Step 1: Remove existing data
        Console.ensureFunctionSuccess("1. Removing an existing data...", this::removeExistingData);

        // Step 2: Insert data into the database
        Console.ensureFunctionSuccess("2. Inserting new data into database...", this::populateDatabase);

        // Add a break line at the end
        Console.addBreakLine(1);
    }

    private void populateDatabase() {
        IntStream.range(0, 10).forEach(i -> {
            User createdUser = userSeeder.insertRow();
            List<LanguageName> languages = languageProficiencyFactory.mockUniqueLanguages(3);

            languages.forEach(language -> {
                languageProficiencySeeder.insertRow(createdUser, language);
            });
        });
    }

    private void removeExistingData() {
        userSeeder.deleteAll();
        languageProficiencySeeder.deleteAll();
    }
}
