package com.backend.ord;

import com.backend.ord.utils.Console;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Objects;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
    @Autowired
    private Environment environment;

    public BackendApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Console.printCyan("The main application has started");
        Console.addBreakLine(1);

        this.verifyEnvironment();
    }

    private void verifyEnvironment() {
        Console.ensureFunctionSuccess(
                "Verify environment",
                () -> {
                    String testingProperty = environment.getProperty("ENV_TEST_PROPERTY");
                    if (!Objects.equals(testingProperty, "1test1")) {
                        throw new RuntimeException();
                    }
                }
        );
    }
}
