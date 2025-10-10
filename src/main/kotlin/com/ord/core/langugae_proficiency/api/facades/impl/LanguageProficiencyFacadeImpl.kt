package com.ord.core.langugae_proficiency.api.facades.impl

import com.ord.core.langugae_proficiency.api.facades.LanguageProficiencyFacade
import com.ord.core.langugae_proficiency.api.requests.CreateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.api.requests.UpdateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.model.LanguageProficiencyDTO
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.LanguageProficiencyMapper
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LanguageProficiencyFacadeImpl(
    private val languageProficiencyService: LanguageProficiencyService,
    private val languageProficiencyMapper: LanguageProficiencyMapper
) : LanguageProficiencyFacade {

    override fun getLanguagesForUser(
        user: UserDTO
    ): Mono<ResponseEntity<Map<LanguageName, LanguageProficiencyLevel>>> {
        return languageProficiencyService
            .findAll(user.id)
            .collectMap({ it.language }, { it.level })
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    override fun createLanguageProficiency(
        user: UserDTO,
        body: CreateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>> {
        val entity = LanguageProficiencyEntity(
            language = body.language,
            level = body.level,
            generativeContentLanguage = body.generativeContentLanguage ?: body.language,
            userId = user.id
        )

        return languageProficiencyService
            .save(entity)
            .map { languageProficiencyMapper.toDTO(it) }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    override fun updateLanguageProficiency(
        user: UserDTO,
        body: UpdateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(user.id, body.language)
            .flatMap { currentProficiency ->
                val updated = currentProficiency.copy(
                    level = body.level ?: currentProficiency.level,
                    generativeContentLanguage = body.generativeContentLanguage
                        ?: currentProficiency.generativeContentLanguage
                )
                languageProficiencyService.save(updated)
            }
            .map { languageProficiencyMapper.toDTO(it) }
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    override fun deleteLanguageProficiency(
        user: UserDTO,
        language: LanguageName
    ): Mono<ResponseEntity<Unit>> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(user.id, language)
            .flatMap { proficiency ->
                languageProficiencyService.deleteById(
                    id = proficiency.id!!,
                    userId = user.id
                )
            }
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }
}
