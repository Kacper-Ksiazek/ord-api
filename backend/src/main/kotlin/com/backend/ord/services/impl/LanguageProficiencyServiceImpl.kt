package com.backend.ord.services.impl

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.LanguageProficiencyService
import org.springframework.stereotype.Service
import java.util.*

@Service
class LanguageProficiencyServiceImpl(
    override val repository: LanguageProficiencyRepository
) : LanguageProficiencyService {
    override fun findUserProficiencyInLanguage(userId: UUID, languageName: LanguageName): LanguageProficiency? {
        return repository.findUserProficiencyInLanguage(userId, languageName)
    }
}