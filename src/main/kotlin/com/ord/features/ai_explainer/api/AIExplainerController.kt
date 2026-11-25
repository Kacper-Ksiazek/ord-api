package com.ord.features.ai_explainer.api

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.ai_explainer.api.facades.AIExplainerFacade
import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseEnhanceRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseSuggestActionsRequest
import com.ord.features.ai_explainer.api.responses.ExplainPhraseEnhanceResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseSuggestActionsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import com.ord.features.ai_explainer.models.explanation.AIExplanationDTO
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @PostMapping("/explain-phrase/enhance", produces = [APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Enhance what the learner typed",
        description = "Rewrite what the learner typed into the explain input into a clearer, more specific request " +
                "(fixing typos and vague wording) before it is sent. The learner reviews the result; nothing is explained yet.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Enhanced request generated",
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
                description = "AI provider failed to produce a result",
                content = [Content()],
            ),
        ],
    )
    fun enhancePrompt(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ExplainPhraseEnhanceRequest,
    ): Mono<ExplainPhraseEnhanceResponse> = aiExplainerFacade.enhancePrompt(body, user)

    @PostMapping("/explain-phrase/suggest-actions", produces = [APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Suggest refine actions for an explanation",
        description = "Classify what the learner asked about (a word or phrase vs a grammar topic or concept) and return " +
                "the refine actions that fit it, most useful first. When an explanationId is given, the detected kind is saved. " +
                "These requests are not recorded in AI usage.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Actions suggested",
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
                description = "AI provider failed to produce a result",
                content = [Content()],
            ),
        ],
    )
    fun suggestActions(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ExplainPhraseSuggestActionsRequest,
    ): Mono<ExplainPhraseSuggestActionsResponse> = aiExplainerFacade.suggestActions(body, user)

    @GetMapping("/explanations", produces = [APPLICATION_JSON_VALUE])
    @Operation(
        summary = "List saved explanations",
        description = "Return the authenticated user's 20 most recent saved explanations, newest first, " +
                "including the follow-ups applied to each."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Saved explanations retrieved successfully"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun getExplanations(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<List<AIExplanationDTO>>> = aiExplainerFacade.getExplanations(user)

    @GetMapping("/explanations/{id}", produces = [APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get a saved explanation",
        description = "Return one saved explanation of the authenticated user, with the follow-ups applied to it."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Explanation retrieved successfully"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Explanation not found (or not the user's)",
                content = [Content()]
            )
        ]
    )
    fun getExplanation(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Explanation ID (the explanationId sent with the explain request)",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID
    ): Mono<AIExplanationDTO> = aiExplainerFacade.getExplanation(id, user)

    @DeleteMapping("/explanations/{id}")
    @Operation(
        summary = "Delete a saved explanation",
        description = "Delete an explanation saved automatically after an explain stream finished. " +
                "Idempotent: succeeds even when the explanation does not exist or belongs to another user."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Explanation deleted (or already absent)",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun deleteExplanation(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Explanation ID (the explanationId sent with the explain request)",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID
    ): Mono<ResponseEntity<Unit>> = aiExplainerFacade.deleteExplanation(id, user)
}
