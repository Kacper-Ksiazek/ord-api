package com.ord.core.word.api.capture

import com.ord.core.word.api.capture.facades.WordCaptureFacade
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/public/words")
@Tag(name = "2. Words: Public Capture", description = "Anonymous bulk word capture by user email")
class PublicWordsController(
    private val wordCaptureFacade: WordCaptureFacade,
) {
    @PostMapping("/bulk-create")
    @Operation(summary = "Public bulk capture words for a user identified by email")
    fun publicBulkCreate(
        @Valid @RequestBody body: PublicWordsBulkCaptureRequest,
    ): Mono<ResponseEntity<Unit>> = wordCaptureFacade.publicBulkCapture(body)
}
