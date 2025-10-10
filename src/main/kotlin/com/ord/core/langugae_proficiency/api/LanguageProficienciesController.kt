package com.ord.core.langugae_proficiency.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.langugae_proficiency.api.facades.LanguageProficiencyFacade
import com.ord.core.langugae_proficiency.api.requests.CreateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.api.requests.UpdateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.model.LanguageProficiencyDTO
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/language-proficiencies")
class LanguageProficienciesController(
    private val languageProficiencyFacade: LanguageProficiencyFacade
) {
    @GetMapping
    fun getLanguagesForUser(
        @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<Map<LanguageName, LanguageProficiencyLevel>>> =
        languageProficiencyFacade.getLanguagesForUser(user)

    @PostMapping
    fun createLanguageProficiency(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>> =
        languageProficiencyFacade.createLanguageProficiency(user, body)

    @PatchMapping
    fun updateLanguageProficiency(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>> =
        languageProficiencyFacade.updateLanguageProficiency(user, body)

    @DeleteMapping("/{language}")
    fun deleteLanguageProficiency(
        @AuthenticatedUser user: UserDTO,
        @PathVariable language: LanguageName
    ): Mono<ResponseEntity<Unit>> =
        languageProficiencyFacade.deleteLanguageProficiency(user, language)
}
