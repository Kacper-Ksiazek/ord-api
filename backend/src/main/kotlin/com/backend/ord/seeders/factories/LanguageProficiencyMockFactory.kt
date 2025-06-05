package com.backend.ord.seeders.factories

import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.seeders.EnumValuesGenerator
import com.backend.ord.shared.utils.EnumUtils.getRandomValue
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyMockFactory(
    val userMockFactory: UserMockFactory
) : AbstractFactory() {
    fun mockUniqueLanguages(n: Int): List<LanguageName> {
        return EnumValuesGenerator.getNRandomUniqueValuesFromEnum<LanguageName>(n)
    }

    fun mockEntity(
        user: UserEntity = userMockFactory.mockEntity()
    ): LanguageProficiencyEntity {
        // Return a new language proficiency entity
        return LanguageProficiencyEntity(
            language = EnumValuesGenerator.mockLanguageName(),
            proficiency = EnumValuesGenerator.mockProficiencyLevel(),
            user = user,
            generativeContentLanguage = LanguageName::class.getRandomValue()
        )
    }
}
