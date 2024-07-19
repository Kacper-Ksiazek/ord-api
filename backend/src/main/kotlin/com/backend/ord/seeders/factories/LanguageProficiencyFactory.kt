package com.backend.ord.seeders.factories

import com.backend.ord.domain.entities.*
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyFactory(
    val userMockFactory: UserMockFactory
) : AbstractFactory() {
    fun mockLanguageName(): LanguageName {
        return LanguageName.entries[faker.random().nextInt(LanguageName.entries.size)]
    }

    fun mockProficiencyLevel(): LanguageProficiencyLevel {
        return LanguageProficiencyLevel.entries[faker.random()
            .nextInt(LanguageProficiencyLevel.entries.size)]
    }

    fun mockUniqueLanguages(N: Int): List<LanguageName> {
        return FactoryUtils.getNRandomUniqueValuesFromEnum(LanguageName::class.java, N)
    }

    fun mockEntity(
        user: User = userMockFactory.mockEntity()
    ): LanguageProficiency {
        // Return a new language proficiency entity
        return LanguageProficiency(
            language = mockLanguageName(),
            proficiency = mockProficiencyLevel(),
            user = user
        )
    }
}
