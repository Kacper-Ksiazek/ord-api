package com.backend.ord.seeders

import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import com.github.javafaker.Faker

object EnumValuesGenerator {
    private val faker: Faker = Faker()

    fun mockLanguageName(): LanguageName {
        return LanguageName.entries[faker.random().nextInt(LanguageName.entries.size)]
    }

    fun mockProficiencyLevel(): LanguageProficiencyLevel {
        return LanguageProficiencyLevel.entries[faker.random()
            .nextInt(LanguageProficiencyLevel.entries.size)]
    }

    inline fun <reified T : Enum<T>> getNRandomUniqueValuesFromEnum(n: Int): List<T> {
        // Get the values of the enum
        val enumConstants = enumValues<T>()

        // Validate N
        require(n in 1..enumConstants.size) { "N must be greater than 0 and less than or equal to the number of elements in the enum" }

        // Return a shuffled sublist of the first N elements
        return enumConstants.toList().shuffled().take(n)
    }

}