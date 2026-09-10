package com.ord.core.word.api.capture.facades.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.api.capture.facades.WordCaptureFacade
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordCaptureFacadeImpl(
    private val wordService: com.ord.core.word.services.WordService,
    private val userRepository: UserRepository,
) : WordCaptureFacade {
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
                wordService.bulkCaptureWords(requests, user.id!!, isFromUnverifiedSource = true)
            }
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.NO_CONTENT).build<Unit>() })
    }

    override fun getOverview(userId: UUID, language: LanguageName?): Mono<ResponseEntity<WordOverviewResponse>> {
        return wordService.countOverview(userId, language)
            .map {
                WordOverviewResponse(
                    total = it.total,
                    activeCount = it.activeCount,
                    pendingCount = it.pendingCount,
                    unverifiedSourceCount = it.unverifiedSourceCount,
                    bookmarkedCount = it.bookmarkedCount,
                )
            }
            .map { ResponseEntity.ok(it) }
    }

    override fun activateMany(userId: UUID, body: ActivateManyWordsRequest): Mono<ResponseEntity<Unit>> {
        return wordService.activateManyWords(body.ids.toSet(), userId)
            .map { ResponseEntity.ok().build<Unit>() }
    }
}
