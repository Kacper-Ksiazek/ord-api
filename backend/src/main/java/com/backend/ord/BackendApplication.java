package com.backend.ord;

import com.backend.ord.seeders.entities.LanguageProficiencySeeder;
import com.backend.ord.seeders.entities.UserSeeder;
import com.backend.ord.seeders.factories.LanguageProficiencyFactory;
import com.backend.ord.utils.Console;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor
public class BackendApplication implements CommandLineRunner{
    private final UserSeeder userSeeder;
    private final LanguageProficiencySeeder languageProficiencySeeder;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Console.printCyan("Main application started");
        Console.addBreakLine(3);
        //
        // TODO: something creeezy 🚀
    }
}
