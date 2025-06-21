package com.ord.core.langugae_proficiency.service

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.shared.services.UserResourceService
import java.util.*

interface LanguageProficiencyService : UserResourceService<LanguageProficiencyEntity> {
    /**
     * Finds the user's proficiency in the specified language or returns null if the user has no proficiency in the language.
     */
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiencyEntity?

    /**
     * Finds the user's proficiency in the specified language or throws an exception if the user has no proficiency in the language.
     * @throws BadRequestException if the user has no proficiency in the language.
     */
    fun findUserProficiencyInLanguageOrThrow(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiencyEntity
}