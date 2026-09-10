package com.ord.core.word.api.capture

import com.ord.config.OpenApiSecurity
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.capture.facades.WordCaptureFacade
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/words")
@Tag(name = "2. Words: Capture", description = "Word overview and pending activation workflow")
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class WordCaptureController(
    private val wordCaptureFacade: WordCaptureFacade,
) {
    @GetMapping("/overview")
    fun getOverview(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @RequestParam(required = false) language: LanguageName?,
    ): Mono<ResponseEntity<WordOverviewResponse>> = wordCaptureFacade.getOverview(user.id, language)

    @PatchMapping("/activate-many")
    fun activateMany(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ActivateManyWordsRequest,
    ): Mono<ResponseEntity<Unit>> = wordCaptureFacade.activateMany(user.id, body)
}
