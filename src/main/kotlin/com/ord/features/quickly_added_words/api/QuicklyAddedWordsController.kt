package com.ord.features.quickly_added_words.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.quickly_added_words.api.facades.QAWFacade
import com.ord.features.quickly_added_words.api.requests.ApproveManyQAWRequest
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.UpdateQAWRequest
import com.ord.features.quickly_added_words.api.responses.QAWOverviewResponse
import com.ord.features.quickly_added_words.api.responses.QAWPaginatedDataResponse
import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
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
import java.util.*

@RestController
@RequestMapping("/api/v1/quickly-added-words")
@Tag(
    name = "3. QAW: Authenticated",
    description = "Rapidly add and manage words for later processing and approval (requires authentication)"
)
@SecurityRequirement(name = "bearer-jwt")
class QuicklyAddedWordsController(
    private val qawFacade: QAWFacade,
) {
    // -------
    // CREATE
    // -------

    @PostMapping("/")
    @Operation(
        summary = "Create a quickly added word",
        description = "Rapidly adds a new word with minimal details for later processing."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Word created successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = QuicklyAddedWordDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid word data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun createOne(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateQAWRequest
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>> = qawFacade.createOne(
        userId = user.id,
        body = body
    )

    @PostMapping("/bulk-create")
    @Operation(
        summary = "Bulk create quickly added words",
        description = "Creates multiple words at once for rapid vocabulary collection."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Words created successfully",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(responseCode = "400", description = "Invalid word data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun bulkCreate(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<CreateQAWRequest>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> = qawFacade.bulkCreate(
        userId = user.id,
        body = body
    )

    // -------
    // READ
    // -------

    @GetMapping("/")
    @Operation(
        summary = "Get quickly added words with pagination",
        description = "Retrieves a paginated list of quickly added words for the authenticated user. Optionally filter by approval status. When isApproved is omitted, unapprovedCount is included in the response."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Words retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = QAWPaginatedDataResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun getManyQAWs(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(required = false) page: Int?,
        @Parameter(description = "Number of items per page", example = "20") @RequestParam(required = false) perPage: Int?,
        @Parameter(description = "Filter by approval status. When omitted, all words are returned and unapprovedCount is included.", example = "false") @RequestParam(required = false) isApproved: Boolean?,
    ): Mono<ResponseEntity<QAWPaginatedDataResponse>> = qawFacade.getManyQAWs(
        userId = user.id,
        page = page,
        perPage = perPage,
        isApproved = isApproved,
    )

    @GetMapping("/overview")
    @Operation(
        summary = "Get quickly added words overview",
        description = "Returns total counts of quickly added words split by approval status."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Overview retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = QAWOverviewResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun getOverview(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<QAWOverviewResponse>> = qawFacade.getOverview(userId = user.id)

    // -------
    // UPDATE
    // -------

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update a quickly added word",
        description = "Updates the content of a specific quickly added word."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Word updated successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = QuicklyAddedWordDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid update data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Word not found", content = [Content()])
        ]
    )
    fun updateOne(
        @Parameter(description = "Word ID to update") @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateQAWRequest
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>> = qawFacade.updateOne(
        userId = user.id,
        qawId = id,
        body = body
    )

    @PatchMapping("/bulk-update")
    @Operation(
        summary = "Bulk update quickly added words",
        description = "Updates multiple words at once using a map of word IDs to new word values."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Words updated successfully",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(responseCode = "400", description = "Invalid update data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun bulkUpdate(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: Map<UUID, String>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> = qawFacade.bulkUpdate(
        userId = user.id,
        body = body.map { (id, word) -> Pair(id, word) }
    )

    @PatchMapping("/approve-many")
    @Operation(
        summary = "Approve multiple quickly added words",
        description = "Marks multiple words as approved. Returns 400 when ids is empty. On success (200), only IDs that exist and belong to the authenticated user are updated; foreign or non-existent IDs are silently skipped."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Words approved successfully"),
            ApiResponse(responseCode = "400", description = "Invalid approval data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun approveMany(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ApproveManyQAWRequest
    ): Mono<ResponseEntity<Unit>> = qawFacade.approveMany(
        userId = user.id,
        body = body
    )

    // -------
    // DELETE
    // -------

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a quickly added word",
        description = "Permanently removes a quickly added word from the user's collection."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Word deleted successfully"),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Word not found", content = [Content()])
        ]
    )
    fun deleteOne(
        @Parameter(description = "Word ID to delete") @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<Unit>> = qawFacade.deleteOne(
        userId = user.id,
        qawId = id
    )

    @PostMapping("/bulk-delete")
    @Operation(
        summary = "Bulk delete quickly added words",
        description = "Deletes multiple words at once using a list of word IDs. Returns 400 when the list is empty. On success (200), only IDs that exist and belong to the authenticated user are deleted; foreign or non-existent IDs are silently skipped."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Words deleted successfully"),
            ApiResponse(responseCode = "400", description = "Invalid delete data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun bulkDelete(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<UUID>
    ): Mono<ResponseEntity<Unit>> = qawFacade.bulkDelete(
        userId = user.id,
        body = body
    )
}