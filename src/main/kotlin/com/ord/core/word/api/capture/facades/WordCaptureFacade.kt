package com.ord.core.word.api.capture.facades

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.enums.WordStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface WordCaptureFacade {
    fun captureOne(userId: UUID, body: CaptureWordRequest): Mono<ResponseEntity<WordDTO>>
    fun bulkCapture(userId: UUID, body: List<CaptureWordRequest>): Mono<ResponseEntity<List<WordDTO>>>
    fun publicBulkCapture(body: PublicWordsBulkCaptureRequest): Mono<ResponseEntity<Unit>>
    fun getCapturedWords(
        userId: UUID,
        page: Int?,
        perPage: Int?,
        status: WordStatus?,
        language: LanguageName?,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>>
    fun getOverview(userId: UUID): Mono<ResponseEntity<WordOverviewResponse>>
    fun updateCaptured(userId: UUID, wordId: UUID, body: UpdateCapturedWordRequest): Mono<ResponseEntity<WordDTO>>
    fun bulkUpdateSourceWords(userId: UUID, body: List<Pair<UUID, String>>): Mono<ResponseEntity<List<WordDTO>>>
    fun activateOne(userId: UUID, wordId: UUID): Mono<ResponseEntity<WordDTO>>
    fun activateMany(userId: UUID, body: ActivateManyWordsRequest): Mono<ResponseEntity<Unit>>
    fun deleteCaptured(userId: UUID, wordId: UUID): Mono<ResponseEntity<Unit>>
    fun bulkDelete(userId: UUID, ids: List<UUID>): Mono<ResponseEntity<Unit>>
}
