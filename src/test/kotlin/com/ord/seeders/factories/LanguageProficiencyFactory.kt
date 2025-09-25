package com.ord.seeders.factories

/*

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserEntity
import com.ord.seeders.factories.bases.FactoryBase
import com.ord.shared.utils.EnumUtils.getNRandomUniqueValues
import com.ord.shared.utils.EnumUtils.getRandomValue
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyFactory(
    val userMockFactory: UserFactory
) : FactoryBase() {
    fun mockUniqueLanguages(n: Int): List<LanguageName> {
        return LanguageName::class.getNRandomUniqueValues(n)
    }

    fun mockEntity(
        user: UserEntity = userMockFactory.mockEntity()
    ): LanguageProficiencyEntity {
        // Return a new language proficiency entity
        return LanguageProficiencyEntity(
            language = LanguageName::class.getRandomValue(),
            proficiency = LanguageProficiencyLevel::class.getRandomValue(),
            user = user,
            generativeContentLanguage = LanguageName::class.getRandomValue()
        )
    }
}


 */