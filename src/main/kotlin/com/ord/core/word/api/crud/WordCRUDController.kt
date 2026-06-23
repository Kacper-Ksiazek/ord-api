package com.ord.core.word.api.crud

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.crud.facades.WordBankManagementFacade
import com.ord.core.word.api.crud.facades.WordCRUDFacade
import com.ord.core.word.api.crud.facades.WordPropertyToggleFacade
import com.ord.core.word.api.crud.requests.dto.ChangeBankForMultipleWordsRequest
import com.ord.core.word.api.crud.requests.dto.ChangeBankForSingleWordRequest
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.crud.requests.dto.UpdateWordRequest
import com.ord.core.word.api.crud.requests.dto.WordBulkActionRequest
import com.ord.core.word.api.crud.requests.enums.WordToggleableProperty
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.WordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/words")
@Tag(
    name = "2. Words: CRUD",
    description = "Create, read, update, and delete vocabulary words with advanced filtering and bulk operations"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class WordCRUDController(
    private val wordCRUDFacade: WordCRUDFacade,
    private val wordPropertyToggleFacade: WordPropertyToggleFacade,
    private val wordBankManagementFacade: WordBankManagementFacade,
) {
    // -------
    // CRUD
    // -------

    @PostMapping("/get-many-words")
    @Operation(
        summary = "Get words with advanced filtering",
        description = "Retrieve paginated list of words with filtering by language, bank, type, and search query"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Words retrieved successfully"
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun getAllWords(
        @RequestBody @Valid requestBody: GetManyWordsRequest,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<PaginatedDataResponse<WordListItem>>> = wordCRUDFacade.getManyWords(
        requestBody = requestBody,
        userId = user.id
    )


    @GetMapping("/{id}")
    @Operation(
        summary = "Get word by ID",
        description = "Retrieve detailed information about a specific word"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word retrieved successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Word not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun getWord(
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<SingleWordResponse>> = wordCRUDFacade.getSingleWord(
        id = id,
        userId = user.id
    )


    @PostMapping("/")
    @Operation(
        summary = "Create a new word",
        description = "Add a new vocabulary word to the user's collection"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word created successfully"
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
    fun createWord(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateWordRequest
    ): Mono<ResponseEntity<WordDTO>> {
        return wordCRUDFacade.createWord(body, user)
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update a word",
        description = "Update an existing word's information"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word updated successfully"
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
    fun updateWord(
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateWordRequest
    ): Mono<ResponseEntity<WordDTO>> {
        return wordCRUDFacade.updateWord(
            id = id,
            body = body,
            userId = user.id
        )
    }


    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a word",
        description = "Permanently delete a word from the user's collection"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word deleted successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Word not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun deleteWord(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID
    ): Mono<ResponseEntity<Unit>> = wordCRUDFacade.deleteWord(
        id = id,
        userId = user.id
    )


    // -------
    // Change Bank
    // -------

    @PostMapping("/{id}/change-bank")
    @Operation(
        summary = "Change word bank",
        description = "Move a word to a different learning bank"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Word bank changed successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Word not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun changeWordBank(
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ChangeBankForSingleWordRequest
    ): Mono<ResponseEntity<Unit>> {
        return wordBankManagementFacade.changeBankOfOneWord(id, body, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    @PostMapping("/change-bank-for-multiple-words")
    @Operation(
        summary = "Change bank for multiple words",
        description = "Move multiple words to a different learning bank in a single operation"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Words bank changed successfully"
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun changeBankForMultipleWords(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ChangeBankForMultipleWordsRequest
    ): Mono<ResponseEntity<Unit>> {
        return wordBankManagementFacade.changeBankOfMultipleWords(body, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    // -------
    // Toggle properties
    // -------

    @PostMapping("/{id}/toggle-property")
    @Operation(
        summary = "Toggle word property",
        description = "Toggle a boolean property for a single word (e.g., favorite, learned)"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Property toggled successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Word not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun togglePropertyForOneWord(
        @Parameter(
            description = "Word ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Property to toggle",
            example = "FAVORITE"
        ) @RequestParam(required = true) property: WordToggleableProperty
    ): Mono<ResponseEntity<Unit>> = wordPropertyToggleFacade.togglePropertyForOneWord(
        id = id,
        property = property,
        userId = user.id
    )


    @PostMapping("/toggle-property-for-multiple-words")
    @Operation(
        summary = "Toggle property for multiple words",
        description = "Toggle a boolean property for multiple words in a single operation"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Property toggled successfully for all words"
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun togglePropertyForManyWords(
        @Parameter(
            description = "Property to toggle",
            example = "FAVORITE"
        ) @RequestParam(required = true) property: WordToggleableProperty,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: WordBulkActionRequest
    ): Mono<ResponseEntity<Unit>> =
        wordPropertyToggleFacade.togglePropertyForMultipleWords(
            body = body,
            property = property,
            userId = user.id
        )
}