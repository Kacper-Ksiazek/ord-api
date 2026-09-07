package com.ord.core.word.api.capture.facades

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.models.word.WordDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface WordCaptureFacade {
    fun capture(userId: UUID, body: List<CaptureWordRequest>): Mono<ResponseEntity<List<WordDTO>>>

    fun publicBulkCapture(body: PublicWordsBulkCaptureRequest): Mono<ResponseEntity<Unit>>

    fun getOverview(userId: UUID, language: LanguageName? = null): Mono<ResponseEntity<WordOverviewResponse>>

    fun updateCaptured(userId: UUID, wordId: UUID, body: UpdateCapturedWordRequest): Mono<ResponseEntity<WordDTO>>

    fun bulkUpdateSourceWords(userId: UUID, body: List<Pair<UUID, String>>): Mono<ResponseEntity<List<WordDTO>>>

    fun activateMany(userId: UUID, body: ActivateManyWordsRequest): Mono<ResponseEntity<Unit>>

    fun deleteCaptured(userId: UUID, wordId: UUID): Mono<ResponseEntity<Unit>>

    fun bulkDelete(userId: UUID, ids: List<UUID>): Mono<ResponseEntity<Unit>>
}
