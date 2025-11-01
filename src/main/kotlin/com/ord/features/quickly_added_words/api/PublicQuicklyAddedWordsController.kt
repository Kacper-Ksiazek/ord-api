package com.ord.features.quickly_added_words.api

import com.ord.features.quickly_added_words.api.facades.PublicQAWFacade
import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/public/quickly-added-words")
@Tag(
    name = "3. QAW: Public",
    description = "Public endpoints for quickly adding words without authentication - ideal for browser extensions and quick capture tools"
)
class PublicQuicklyAddedWordsController(
    private val publicQAWFacade: PublicQAWFacade,
) {
    @PostMapping("/bulk-create")
    @Operation(
        summary = "Bulk create words (public)",
        description = "Rapidly add multiple words to the database without authentication using a shared secret key"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "Words created successfully",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request data or authentication failed",
            content = [Content()]
        )
    ])
    fun publicBulkCreate(
        @Valid @RequestBody body: PublicQAWBulkCreateRequest
    ): Mono<ResponseEntity<Void>> = publicQAWFacade.publicBulkCreate(body)
}