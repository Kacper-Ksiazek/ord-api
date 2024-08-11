package com.backend.ord.seeders.factories

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.seeders.EnumValuesGenerator
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyMockFactory(
    val userMockFactory: UserMockFactory
) : AbstractFactory() {
    fun mockUniqueLanguages(N: Int): List<LanguageName> {
        return EnumValuesGenerator.getNRandomUniqueValuesFromEnum<LanguageName>(N)
    }

    fun mockEntity(
        user: User = userMockFactory.mockEntity()
    ): LanguageProficiency {
        // Return a new language proficiency entity
        return LanguageProficiency(
            language = EnumValuesGenerator.mockLanguageName(),
            proficiency = EnumValuesGenerator.mockProficiencyLevel(),
            user = user
        )
    }
}
