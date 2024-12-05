package com.backend.ord.services

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.services.bases.UserResourceService
import java.util.*

interface LanguageProficiencyService : UserResourceService<LanguageProficiency> {
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiency?
}