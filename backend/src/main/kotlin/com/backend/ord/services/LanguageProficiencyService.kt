package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.services.bases.UserResourceService
import java.util.*

interface LanguageProficiencyService : UserResourceService<LanguageProficiency> {
    /**
     * Finds the user's proficiency in the specified language or returns null if the user has no proficiency in the language.
     */
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiency?

    /**
     * Finds the user's proficiency in the specified language or throws an exception if the user has no proficiency in the language.
     * @throws BadRequestException if the user has no proficiency in the language.
     */
    fun findUserProficiencyInLanguageOrThrow(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiency
}