package com.ord.core.word.api.ai

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.ai.facades.WordAIFacade
import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/words/ai")
@Tag(
    name = "2. Words: AI Generation",
    description = "AI-powered word generation and enhancement for vocabulary learning"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class WordAIController(
    private val wordAIFacade: WordAIFacade
) {
    @PostMapping("/generate-manual")
    @Operation(
        summary = "Generate AI word manual",
        description = "Generate comprehensive AI-powered manual for a word including definitions, examples, and usage"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Word manual generated successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun generateAIManual(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: GenerateWordManualRequest
    ): Mono<ResponseEntity<AIGeneratedWordManual>> {
        return wordAIFacade.generateWordManual(body, user)
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }


    @PostMapping("/suggest-vocabulary", produces = [TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Suggest vocabulary for learning",
        description = "Generate personalized AI-powered vocabulary suggestions based on user's proficiency level and existing vocabulary (streaming response). " +
                "Optionally accepts context to tailor suggestions to specific scenarios (e.g., 'business meetings', 'grocery shopping')."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vocabulary suggestions stream started successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or user does not have proficiency in the requested language",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun suggestVocabulary(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: SuggestVocabularyRequest
    ) = wordAIFacade.suggestVocabulary(body, user)
}