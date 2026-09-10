package com.ord.core.word.api.capture.facades

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface WordCaptureFacade {
    fun publicBulkCapture(body: PublicWordsBulkCaptureRequest): Mono<ResponseEntity<Unit>>

    fun getOverview(userId: UUID, language: LanguageName? = null): Mono<ResponseEntity<WordOverviewResponse>>

    fun activateMany(userId: UUID, body: ActivateManyWordsRequest): Mono<ResponseEntity<Unit>>
}
