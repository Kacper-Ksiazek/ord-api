package com.ord.core.langugae_proficiency.service

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Mono
import java.util.*

interface LanguageProficiencyService : UserResourceService<LanguageProficiencyEntity> {
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: LanguageName
    ): Mono<LanguageProficiencyEntity?>


    fun findUserProficiencyInLanguageOrThrow(
        userId: UUID,
        languageName: LanguageName
    ): Mono<LanguageProficiencyEntity>
}