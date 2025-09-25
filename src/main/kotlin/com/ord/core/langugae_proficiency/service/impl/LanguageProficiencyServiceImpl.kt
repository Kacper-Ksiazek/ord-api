package com.ord.core.langugae_proficiency.service.impl

import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.exceptions.REST.BadRequestException
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class LanguageProficiencyServiceImpl(
    private val repository: LanguageProficiencyRepository,
) : LanguageProficiencyService {
    override val userRepository: UserResourceRepository<LanguageProficiencyEntity> = repository
    override val crudRepository: ReactiveCrudRepository<LanguageProficiencyEntity, UUID> = repository

    override fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: LanguageName
    ): Mono<LanguageProficiencyEntity?> {
        return repository.findUserProficiencyInLanguage(
            userId = userId,
            languageName = languageName.name
        )
    }


    override fun findUserProficiencyInLanguageOrThrow(
        userId: UUID,
        languageName: LanguageName
    ): Mono<LanguageProficiencyEntity> {
        return findUserProficiencyInLanguage(userId, languageName)
            .switchIfEmpty(
                Mono.error(BadRequestException("User does not have any proficiency in the requested language."))
            )
            .map { it!! }
    }
}

