package com.backend.ord.seeders.entities

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.language.LanguageProficiencyLevel
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.seeders.factories.LanguageProficiencyMockFactory
import org.springframework.stereotype.Component

@Component
class LanguageProficiencySeeder(
    private val userSeeder: UserSeeder,
    private val languageProficiencyRepository: LanguageProficiencyRepository,
    private val languageProficiencyFactory: LanguageProficiencyMockFactory
) : SeederInterface<LanguageProficiency> {
    override fun seedOneEntity(data: LanguageProficiency?): LanguageProficiency {
        return languageProficiencyRepository.save(
            data ?: languageProficiencyFactory.mockEntity()
        )
    }

    override fun deleteAll() {
        languageProficiencyRepository.deleteAll()
    }

    fun seedOneEntity(
        user: User,
        languageName: LanguageName? = null,
        generativeContentLanguage: LanguageName? = null,
        languageProficiency: LanguageProficiencyLevel? = null
    ): LanguageProficiency {
        val data = languageProficiencyFactory.mockEntity(user)

        languageName?.also { data.language = it }
        languageProficiency?.also { data.proficiency = it }
        generativeContentLanguage?.also { data.generativeContentLanguage = it }

        return languageProficiencyRepository.save(data)
    }
}
