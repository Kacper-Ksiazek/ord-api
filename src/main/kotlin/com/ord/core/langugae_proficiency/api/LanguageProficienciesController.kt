package com.ord.core.langugae_proficiency.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.langugae_proficiency.api.facades.LanguageProficiencyFacade
import com.ord.core.langugae_proficiency.api.requests.CreateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.api.requests.UpdateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.model.LanguageProficiencyDTO
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/language-proficiencies")
@Tag(
    name = "1. Core: Language Proficiencies",
    description = "Manage user's language learning proficiencies and levels"
)
@SecurityRequirement(name = "bearer-jwt")
class LanguageProficienciesController(
    private val languageProficiencyFacade: LanguageProficiencyFacade
) {
    @GetMapping
    @Operation(
        summary = "Get all language proficiencies",
        description = "Returns a map of all languages and proficiency levels for the authenticated user."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Language proficiencies retrieved successfully"),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun getLanguagesForUser(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<Map<LanguageName, LanguageProficiencyLevel>>> =
        languageProficiencyFacade.getLanguagesForUser(user)

    @PostMapping
    @Operation(
        summary = "Create language proficiency",
        description = "Adds a new language proficiency entry for the authenticated user."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Language proficiency created successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = LanguageProficiencyDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid request data or language already exists", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun createLanguageProficiency(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>> =
        languageProficiencyFacade.createLanguageProficiency(user, body)

    @PatchMapping
    @Operation(
        summary = "Update language proficiency",
        description = "Updates an existing language proficiency level for the authenticated user."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Language proficiency updated successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = LanguageProficiencyDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid request data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Language proficiency not found", content = [Content()])
        ]
    )
    fun updateLanguageProficiency(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateLanguageProficiencyRequest
    ): Mono<ResponseEntity<LanguageProficiencyDTO>> =
        languageProficiencyFacade.updateLanguageProficiency(user, body)

    @DeleteMapping("/{language}")
    @Operation(
        summary = "Delete language proficiency",
        description = "Removes a language proficiency entry for the authenticated user."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Language proficiency deleted successfully"),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Language proficiency not found", content = [Content()])
        ]
    )
    fun deleteLanguageProficiency(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(description = "Language name to delete", example = "ENGLISH")
        @PathVariable language: LanguageName
    ): Mono<ResponseEntity<Unit>> =
        languageProficiencyFacade.deleteLanguageProficiency(user, language)
}
