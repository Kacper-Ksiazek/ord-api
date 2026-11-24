package com.ord.features.ai_explainer.api

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.ai_explainer.api.facades.AIExplainerFacade
import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
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

    @PostMapping("/explain-phrase/follow-up", produces = [TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Follow up on a phrase explanation",
        description = "Stream a follow-up explanation based on a previous explanation and a predefined action " +
                "(simpler wording, more examples, or similar expressions).",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Follow-up explanation stream started successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or user does not have proficiency in the requested language",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()],
            ),
        ],
    )
    fun followUpExplainPhrase(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ExplainPhraseFollowUpRequest,
    ) = aiExplainerFacade.followUpExplainPhrase(body, user)

    @PostMapping("/autocomplete", produces = [APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Autocomplete the explain input",
        description = "Return a short AI-predicted continuation of what the learner has typed into the explain input " +
                "(tab completion). The completion contains only the text to append. These requests are not recorded in AI usage.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Completion generated (may be an empty string)",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or user does not have proficiency in the requested language",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "502",
                description = "AI provider failed to produce a completion",
                content = [Content()],
            ),
        ],
    )
    fun autocompletePhrase(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ExplainPhraseAutocompleteRequest,
    ) = aiExplainerFacade.autocompletePhrase(body, user)
}
