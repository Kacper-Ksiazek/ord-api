package com.backend.ord.seeders

import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.seeders.entities.LanguageProficiencySeeder
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.factories.LanguageProficiencyFactory
import com.backend.ord.utils.Console.addBreakLine
import com.backend.ord.utils.Console.ensureFunctionSuccess
import com.backend.ord.utils.Console.printCyan
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.function.Consumer
import java.util.stream.IntStream

@Component
@Profile("local")
@Order(Int.MIN_VALUE)
class DatabaseSeeder(
    private val userSeeder: UserSeeder,
    private val languageProficiencySeeder: LanguageProficiencySeeder,
    private val languageProficiencyFactory: LanguageProficiencyFactory
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        // Print a message to the console
        printCyan("Seeding database:\n")

        // Step 1: Remove existing data
        ensureFunctionSuccess("1. Removing an existing data...") { this.removeExistingData() }

        // Step 2: Insert data into the database
        ensureFunctionSuccess("2. Inserting new data into database...") { this.populateDatabase() }

        // Add a break line at the end
        addBreakLine(1)
    }

    private fun populateDatabase() {
        IntStream.range(0, 10).forEach { _: Int ->
            val createdUser = userSeeder.insertRow()
            val languages = languageProficiencyFactory.mockUniqueLanguages(3)

            languages.forEach {
                languageProficiencySeeder.insertRow(
                    user = createdUser,
                    languageName = it
                )
            }
        }
    }

    private fun removeExistingData() {
        userSeeder.deleteAll()
        languageProficiencySeeder.deleteAll()
    }
}
