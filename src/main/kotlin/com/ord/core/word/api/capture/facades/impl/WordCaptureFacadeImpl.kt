package com.ord.core.word.api.capture.facades.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.api.capture.facades.WordCaptureFacade
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.services.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordCaptureFacadeImpl(
    private val wordService: WordService,
    private val userRepository: UserRepository,
) : WordCaptureFacade {
    override fun captureOne(userId: UUID, body: CaptureWordRequest): Mono<ResponseEntity<WordDTO>> {
        return wordService.captureWord(body, userId)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    override fun bulkCapture(userId: UUID, body: List<CaptureWordRequest>): Mono<ResponseEntity<List<WordDTO>>> {
        return wordService.bulkCaptureWords(body, userId)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    override fun publicBulkCapture(body: PublicWordsBulkCaptureRequest): Mono<ResponseEntity<Unit>> {
        return userRepository.findByEmail(body.userEmail)
            .switchIfEmpty(Mono.error(UserNotFoundException(email = body.userEmail)))
            .flatMap { user ->
                val requests = body.words.map { item ->
                    CaptureWordRequest(
                        sourceWord = item.sourceWord,
                        language = item.language,
                        translation = item.translation,
                        definition = item.definition,
                        extraMark = item.extraMark,
                        type = item.type,
                    )
                }
                wordService.bulkCaptureWords(requests, user.id!!, WordStatus.CAPTURED)
            }
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.NO_CONTENT).build<Unit>() })
    }

    override fun getCapturedWords(
        userId: UUID,
        page: Int?,
        perPage: Int?,
        status: WordStatus?,
        language: LanguageName?,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>> {
        return wordService.findManyWords(
            userId = userId,
            language = language,
            status = status,
            page = page ?: 0,
            perPage = perPage ?: 50,
        ).map { result ->
            WordsPaginatedDataResponse(
                pagination = result.paginated.pagination,
                data = result.paginated.data,
                capturedCount = result.capturedCount,
            )
        }.map { ResponseEntity.ok(it) }
    }

    override fun getOverview(userId: UUID): Mono<ResponseEntity<WordOverviewResponse>> {
        return wordService.countByStatus(userId)
            .map {
                WordOverviewResponse(
                    total = it.total,
                    activeCount = it.activeCount,
                    capturedCount = it.capturedCount,
                )
            }
            .map { ResponseEntity.ok(it) }
    }

    override fun updateCaptured(
        userId: UUID,
        wordId: UUID,
        body: UpdateCapturedWordRequest,
    ): Mono<ResponseEntity<WordDTO>> {
        return wordService.updateCapturedWord(wordId, userId, body)
            .map { ResponseEntity.ok(it) }
    }

    override fun bulkUpdateSourceWords(
        userId: UUID,
        body: List<Pair<UUID, String>>,
    ): Mono<ResponseEntity<List<WordDTO>>> {
        return wordService.bulkUpdateSourceWords(userId, body)
            .map { ResponseEntity.ok(it) }
    }

    override fun activateOne(userId: UUID, wordId: UUID): Mono<ResponseEntity<WordDTO>> {
        return wordService.activateWord(wordId, userId)
            .map { ResponseEntity.ok(it) }
    }

    override fun activateMany(userId: UUID, body: ActivateManyWordsRequest): Mono<ResponseEntity<Unit>> {
        return wordService.activateManyWords(body.ids.toSet(), userId)
            .map { ResponseEntity.ok().build<Unit>() }
    }

    override fun deleteCaptured(userId: UUID, wordId: UUID): Mono<ResponseEntity<Unit>> {
        return wordService.deleteById(wordId, userId)
            .then(Mono.fromCallable { ResponseEntity.ok().build<Unit>() })
    }

    override fun bulkDelete(userId: UUID, ids: List<UUID>): Mono<ResponseEntity<Unit>> {
        if (ids.isEmpty()) return Mono.error(BadRequestException("No IDs provided for deletion"))
        return Flux.fromIterable(ids.toSet())
            .flatMap { id -> wordService.deleteById(id, userId) }
            .then(Mono.fromCallable { ResponseEntity.ok().build<Unit>() })
    }
}
