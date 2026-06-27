package com.ord.features.quickly_added_words.api

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.quickly_added_words.api.facades.QAWAIFacade
import com.ord.features.quickly_added_words.api.requests.QAWFillGapsRequest
import com.ord.features.quickly_added_words.api.responses.QAWFillGapsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/quickly-added-words/ai")
@Tag(
    name = "3. QAW: Authenticated",
    description = "Rapidly add and manage words for later processing and approval (requires authentication)",
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class QAWAIController(
    private val qawAIFacade: QAWAIFacade,
) {
    @PostMapping("/fill-gaps")
    @Operation(
        summary = "AI-fill missing QAW fields",
        description = "Enriches a batch of words (word-only input) with translation, definition, type, and optional extra mark. " +
            "Does not persist to the database — use bulk-create to save.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Fields enriched successfully (per-item errors may be present in the response)",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = QAWFillGapsResponse::class))],
            ),
            ApiResponse(responseCode = "400", description = "Invalid request or user has no proficiency in the language", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()]),
        ],
    )
    fun fillGaps(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: QAWFillGapsRequest,
    ): Mono<ResponseEntity<QAWFillGapsResponse>> {
        return qawAIFacade.fillGaps(body, user)
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
}
