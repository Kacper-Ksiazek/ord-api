package com.ord.core.langugae_proficiency.api.facades

import com.ord.core.langugae_proficiency.api.requests.CreateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.api.requests.UpdateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.model.LanguageProficiencyDTO
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface LanguageProficiencyFacade {
    fun getLanguagesForUser(
        user: UserDTO
    ): Mono<ResponseEntity<Map<LanguageName, LanguageProficiencyLevel>>>


    fun createLanguageProficiency(
        user: UserDTO,
        body: CreateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>>


    fun updateLanguageProficiency(
        user: UserDTO,
        body: UpdateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>>


    fun deleteLanguageProficiency(
        user: UserDTO,
        language: LanguageName,
    ): Mono<ResponseEntity<Unit>>
}