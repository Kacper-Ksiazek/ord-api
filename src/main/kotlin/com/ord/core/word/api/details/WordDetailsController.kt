package com.ord.core.word.api.details

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.details.facades.WordDetailsFacade
import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word_details.WordDetailsCompactDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/words/{wordId}/details")
@Tag(
    name = "2. Words: Details",
    description = "Manage detailed word information including examples, usage notes, and additional context"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class WordDetailsController(
    private val wordDetailsFacade: WordDetailsFacade
) {
    @PostMapping
    @Operation(
        summary = "Create word details",
        description = "Add detailed information to a word such as examples and usage notes"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word details created successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Word not found",
            content = [Content()]
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
    ])
    fun createWordDetails(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable wordId: UUID,
        @Valid @RequestBody body: CreateWordDetailsRequest
    ): Mono<ResponseEntity<WordDetailsCompactDTO>> =
        wordDetailsFacade.createWordDetails(
            wordId = wordId,
            request = body,
            userId = user.id
        )

    @PatchMapping
    @Operation(
        summary = "Update word details",
        description = "Update detailed information for an existing word"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word details updated successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Word or details not found",
            content = [Content()]
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
    ])
    fun updateWordDetails(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable wordId: UUID,
        @Valid @RequestBody body: UpdateWordDetailsRequest
    ): Mono<ResponseEntity<WordDetailsCompactDTO>> =
        wordDetailsFacade.updateWordDetails(
            wordId = wordId,
            request = body,
            userId = user.id
        )
}