package com.ord.core.word.api.details

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.details.facades.WordDetailsFacade
import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word_details.WordDetailsCompactDTO
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/words/{wordId}/details")
class WordDetailsController(
    private val wordDetailsFacade: WordDetailsFacade
) {
    @PostMapping
    fun createWordDetails(
        @AuthenticatedUser user: UserDTO,
        @PathVariable wordId: UUID,
        @Valid @RequestBody body: CreateWordDetailsRequest
    ): Mono<ResponseEntity<WordDetailsCompactDTO>> =
        wordDetailsFacade.createWordDetails(
            wordId = wordId,
            request = body,
            userId = user.id
        )

    @PatchMapping
    fun updateWordDetails(
        @AuthenticatedUser user: UserDTO,
        @PathVariable wordId: UUID,
        @Valid @RequestBody body: UpdateWordDetailsRequest
    ): Mono<ResponseEntity<WordDetailsCompactDTO>> =
        wordDetailsFacade.updateWordDetails(
            wordId = wordId,
            request = body,
            userId = user.id
        )
}