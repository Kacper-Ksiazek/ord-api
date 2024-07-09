package com.backend.ord.seeders.factories

import com.backend.ord.domain.entities.*
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyFactory : AbstractFactory() {
    fun mockLanguageName(): LanguageName {
        return LanguageName.entries[AbstractFactory.Companion.faker.random().nextInt(LanguageName.entries.size)]
    }

    fun mockProficiencyLevel(): LanguageProficiencyLevel {
        return LanguageProficiencyLevel.entries[AbstractFactory.Companion.faker.random()
            .nextInt(LanguageProficiencyLevel.entries.size)]
    }

    fun mockUniqueLanguages(N: Int): List<LanguageName?>? {
        return FactoryUtils.getNRandomUniqueValuesFromEnum(LanguageName::class.java, N)
    }

    fun mockEntity(user: User?): LanguageProficiency {
        // Validate received user
        require(!(user == null || user.id == null)) { "User must be provided with a valid ID" }

        // Return a new language proficiency entity
        return LanguageProficiency.Companion.builder()
            .language(mockLanguageName())
            .proficiency(mockProficiencyLevel())
            .user(user)
            .build()
    }

    fun mockEntity(): LanguageProficiency {
        // Return a new language proficiency entity
        return LanguageProficiency.Companion.builder()
            .language(mockLanguageName())
            .proficiency(mockProficiencyLevel())
            .user(null)
            .build()
    }
}
