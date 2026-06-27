package com.ord.features.ai_explainer.api

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.ai_explainer.api.facades.AIExplainerFacade
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ai-explainer")
@Tag(
    name = "8. AI Explainer",
    description = "AI-powered explanations of words and phrases for language learning"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class AIExplainerController(
    private val aiExplainerFacade: AIExplainerFacade
) {
    @PostMapping("/explain-phrase", produces = [TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Get AI explanation of a word or phrase",
        description = "Stream a clear, educational explanation of a word or phrase in plain text, " +
                "including translation, definition, examples, and usage notes tailored to the user's proficiency level."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Word/phrase explanation stream started successfully"
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
    fun explainPhrase(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ExplainPhraseRequest
    ) = aiExplainerFacade.explainPhrase(body, user)
}