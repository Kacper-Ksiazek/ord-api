package com.backend.ord.services.impl

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.services.LanguageProficiencyService
import org.springframework.stereotype.Service
import java.util.*

@Service
class LanguageProficiencyServiceImpl(
    override val repository: LanguageProficiencyRepository
) : LanguageProficiencyService {
    override fun findUserProficiencyInLanguage(userId: UUID, languageName: LanguageName): LanguageProficiency? {
        return repository.findUserProficiencyInLanguage(
            userId = userId,
            languageName = languageName.name
        )
    }

    override fun findUserProficiencyInLanguageOrThrow(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiency {
        return findUserProficiencyInLanguage(userId, languageName)
            ?: throw BadRequestException("User does not have any proficiency in the requested language.")
    }
}