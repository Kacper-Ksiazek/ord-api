package com.ord.seeders.entities

import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserEntity
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.seeders.factories.LanguageProficiencyFactory
import org.springframework.stereotype.Component

@Component
class LanguageProficiencySeeder(
    private val userSeeder: UserSeeder,
    private val languageProficiencyRepository: LanguageProficiencyRepository,
    private val languageProficiencyFactory: LanguageProficiencyFactory
) : SeederInterface<LanguageProficiencyEntity> {
    override fun seedOneEntity(data: LanguageProficiencyEntity?): LanguageProficiencyEntity {
        return languageProficiencyRepository.save(
            data ?: languageProficiencyFactory.mockEntity()
        )
    }

    override fun deleteAll() {
        languageProficiencyRepository.deleteAll()
    }

    fun seedOneEntity(
        user: UserEntity,
        languageName: LanguageName? = null,
        generativeContentLanguage: LanguageName? = null,
        languageProficiency: LanguageProficiencyLevel? = null
    ): LanguageProficiencyEntity {
        val data = languageProficiencyFactory.mockEntity(user)

        languageName?.also { data.language = it }
        languageProficiency?.also { data.proficiency = it }
        generativeContentLanguage?.also { data.generativeContentLanguage = it }

        return languageProficiencyRepository.save(data)
    }
}
