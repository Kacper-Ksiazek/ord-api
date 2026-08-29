package com.ord.core.word.api.capture

import com.ord.config.OpenApiSecurity
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.capture.facades.WordCaptureFacade
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.enums.WordStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/words")
@Tag(name = "2. Words: Capture", description = "Quick capture, overview, and activation workflow")
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class WordCaptureController(
    private val wordCaptureFacade: WordCaptureFacade,
) {
    @PostMapping("/capture")
    fun captureOne(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CaptureWordRequest,
    ): Mono<ResponseEntity<WordDTO>> = wordCaptureFacade.captureOne(user.id, body)

    @PostMapping("/bulk-capture")
    fun bulkCapture(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<CaptureWordRequest>,
    ): Mono<ResponseEntity<List<WordDTO>>> = wordCaptureFacade.bulkCapture(user.id, body)

    @GetMapping("/overview")
    fun getOverview(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<WordOverviewResponse>> = wordCaptureFacade.getOverview(user.id)

    @GetMapping("/captured")
    @Operation(summary = "List captured/active words for inbox workflow")
    fun getCapturedWords(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) perPage: Int?,
        @RequestParam(required = false) status: WordStatus?,
        @RequestParam(required = false) language: LanguageName?,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>> = wordCaptureFacade.getCapturedWords(
        userId = user.id,
        page = page,
        perPage = perPage,
        status = status,
        language = language,
    )

    @PatchMapping("/{id}/activate")
    fun activateOne(
        @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<WordDTO>> = wordCaptureFacade.activateOne(user.id, id)

    @PatchMapping("/activate-many")
    fun activateMany(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ActivateManyWordsRequest,
    ): Mono<ResponseEntity<Unit>> = wordCaptureFacade.activateMany(user.id, body)

    @PatchMapping("/{id}/capture")
    fun updateCaptured(
        @PathVariable id: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateCapturedWordRequest,
    ): Mono<ResponseEntity<WordDTO>> = wordCaptureFacade.updateCaptured(user.id, id, body)

    @PatchMapping("/bulk-update-source")
    fun bulkUpdateSourceWords(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: Map<UUID, String>,
    ): Mono<ResponseEntity<List<WordDTO>>> = wordCaptureFacade.bulkUpdateSourceWords(
        user.id,
        body.map { (id, word) -> id to word },
    )
}
