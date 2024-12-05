package com.backend.ord.seeders

import com.backend.ord.domain.entities.User
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.language.LanguageProficiencyLevel
import com.backend.ord.enums.UserRole
import com.backend.ord.seeders.entities.LanguageProficiencySeeder
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.factories.LanguageProficiencyMockFactory
import com.backend.ord.seeders.mocks.MockedEntitySummary
import com.backend.ord.seeders.mocks.MocksFromJSONFiles
import com.backend.ord.utils.Console
import com.backend.ord.utils.Console.addBreakLine
import com.backend.ord.utils.Console.ensureFunctionSuccess
import com.backend.ord.utils.Console.printCyan
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.stream.IntStream

@Component
@Profile("local")
@Order(Int.MIN_VALUE)
class DatabaseSeeder(
    private val userSeeder: UserSeeder,
    private val passwordEncoder: PasswordEncoder,
    private val languageProficiencySeeder: LanguageProficiencySeeder,
    private val languageProficiencyFactory: LanguageProficiencyMockFactory,
    private val mocksFromJSONFiles:  MocksFromJSONFiles
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        // Print a message to the console
        printCyan("Seeding database:\n")

        val currentDirectory = System.getProperty("user.dir")
        println("Current working directory: $currentDirectory")


        // Step 1: Remove existing data
        ensureFunctionSuccess("1. Removing an existing data...") { this.removeExistingData() }

        // Step 2: Insert data into the database
        ensureFunctionSuccess("2. Inserting new data into database...") { this.populateDatabase() }

        // Step 3: Create my user
        ensureFunctionSuccess("3. Creating Kacper Książek user...") { this.createMyUser() }

        // Add a break line at the end
        addBreakLine(1)

        // Print a message to the console
        Console.printGreen("Database seeded successfully!\n")
    }

    private fun populateDatabase(): String {
        val numberOfUsers = 10;
        val numberOfLanguagesPerUser = 3;

        IntStream.range(0, numberOfUsers).forEach { _: Int ->
            val createdUser = userSeeder.seedOneEntity()
            val languages = languageProficiencyFactory.mockUniqueLanguages(numberOfLanguagesPerUser)

            languages.forEach {
                languageProficiencySeeder.seedOneEntity(
                    user = createdUser,
                    languageName = it
                )
            }
        }

        return listOf(
            "$numberOfUsers users mocked",
            "${numberOfUsers * numberOfLanguagesPerUser} language mocked"
        ).joinToString(separator = "\n") {
            "   - $it"
        }
    }

    private fun createMyUser(): String {
        // 1. Create a user
        val kacper = userSeeder.seedOneEntity(
            User(
                name = "Kacper Książek",
                email = "kacper.b.ksiazek@gmail.com",
                password = passwordEncoder.encode("zaq1"),
                nativeLanguage = LanguageName.POLISH,
                role = UserRole.ADMIN
            )
        )

        // 2. Create a language proficiency
        languageProficiencySeeder.seedOneEntity(
            user = kacper,
            languageName = LanguageName.ENGLISH,
            languageProficiency = LanguageProficiencyLevel.C1,
            generativeContentLanguage = LanguageName.ENGLISH
        )

        languageProficiencySeeder.seedOneEntity(
            user = kacper,
            languageName = LanguageName.GERMAN,
            languageProficiency = LanguageProficiencyLevel.A2,
            generativeContentLanguage = LanguageName.ENGLISH
        )

        languageProficiencySeeder.seedOneEntity(
            user = kacper,
            languageName = LanguageName.SLOVENIAN,
            languageProficiency = LanguageProficiencyLevel.A1,
            generativeContentLanguage = LanguageName.ENGLISH
        )

        // 3. Seed database with data from json files
        val dataFromJSON: List<MockedEntitySummary> = mocksFromJSONFiles.run(kacper)



        return (listOf(
            "User account created successfully with email: ${kacper.email}",
            "Languages proficiency: English ( C1 ), German ( A2 ), Slovenian ( A1 )",
        ) + dataFromJSON.map { "${it.name}: ${it.amount}" }).joinToString(separator = "\n") {
            "   - $it"
        }
    }

    private fun removeExistingData() {
        userSeeder.deleteAll()
        languageProficiencySeeder.deleteAll()
    }
}
