package com.backend.ord.seeders.entities

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.seeders.factories.LanguageProficiencyFactory
import org.springframework.stereotype.Component

@Component
class LanguageProficiencySeeder(
    private val userSeeder: UserSeeder,
    private val languageProficiencyRepository: LanguageProficiencyRepository,
    private val languageProficiencyFactory: LanguageProficiencyFactory
) : SeederInterface<LanguageProficiency> {
    override fun insertRow(): LanguageProficiency {
        // Generate user to fill the foreign key constraint
        val user = userSeeder.insertRow()
        return insertRow(user)
    }

    override fun deleteAll() {
        languageProficiencyRepository.deleteAll()
    }

    fun insertRow(user: User?): LanguageProficiency {
        val data = languageProficiencyFactory.mockEntity(user)
        return languageProficiencyRepository.save(data)
    }

    fun insertRow(user: User?, language: LanguageName?): LanguageProficiency {
        val data = languageProficiencyFactory.mockEntity(user)
        data.language = language

        return languageProficiencyRepository.save(data)
    }
}
