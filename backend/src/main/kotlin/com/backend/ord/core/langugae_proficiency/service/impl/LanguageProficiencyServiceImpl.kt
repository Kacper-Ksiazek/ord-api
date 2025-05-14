package com.backend.ord.core.langugae_proficiency.service.impl

import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.repositories.LanguageProficiencyRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class LanguageProficiencyServiceImpl(
    override val repository: LanguageProficiencyRepository
) : LanguageProficiencyService {
    override fun findUserProficiencyInLanguage(userId: UUID, languageName: LanguageName): LanguageProficiencyEntity? {
        return repository.findUserProficiencyInLanguage(
            userId = userId,
            languageName = languageName.name
        )
    }

    override fun findUserProficiencyInLanguageOrThrow(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiencyEntity {
        return findUserProficiencyInLanguage(userId, languageName)
            ?: throw BadRequestException("User does not have any proficiency in the requested language.")
    }
}