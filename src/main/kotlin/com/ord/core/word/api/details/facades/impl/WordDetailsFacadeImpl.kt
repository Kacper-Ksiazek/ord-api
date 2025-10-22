package com.ord.core.word.api.details.facades.impl

import com.ord.core.word.api.details.facades.WordDetailsFacade
import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word_details.WordDetailsCompactDTO
import com.ord.core.word.models.word_details.WordDetailsEntity
import com.ord.core.word.models.word_details.WordDetailsMapper
import com.ord.core.word.models.word_details.toCompact
import com.ord.core.word.repositories.WordDetailsRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.core.word.services.WordDetailsService
import com.ord.exceptions.REST.ConflictException
import com.ord.exceptions.REST.NotFoundException
import io.r2dbc.postgresql.codec.Json
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordDetailsFacadeImpl(
    private val wordDetailsService: WordDetailsService,
    private val wordRepository: WordRepository,
    private val wordDetailsRepository: WordDetailsRepository,
    private val wordDetailsMapper: WordDetailsMapper
) : WordDetailsFacade {

    override fun createWordDetails(
        wordId: UUID,
        request: CreateWordDetailsRequest,
        userId: UUID
    ): Mono<ResponseEntity<WordDetailsCompactDTO>> {
        return wordRepository
            .findByIdAndUserId(
                id = wordId,
                userId = userId
            )
            .switchIfEmpty(
                Mono.error(NotFoundException("Word with id $wordId not found"))
            )
            .flatMap {
                wordDetailsRepository.existsByWordIdAndUserId(
                    wordId = wordId,
                    userId = userId
                )
            }
            .flatMap { exists ->
                if (exists) {
                    Mono.error(ConflictException("Word details already exist for word with id $wordId"))
                } else {
                    val wordDetailsToSave = WordDetailsEntity(
                        wordId = wordId,
                        useCases = wordDetailsMapper.serializeStringSet(request.useCases),
                        synonyms = wordDetailsMapper.serializeStringSet(request.synonyms),
                        antonyms = wordDetailsMapper.serializeStringSet(request.antonyms),
                        commonMistakes = wordDetailsMapper.serializeStringSet(request.commonMistakes),
                        exampleSentences = wordDetailsMapper.serializeExampleSentences(request.exampleSentences),
                        collocations = wordDetailsMapper.serializeCollocations(request.collocations),
                        pronunciation = request.pronunciation?.let { Json.of(wordDetailsMapper.jsonObjectMapper.writeValueAsString(it)) },
                        grammar = request.grammar?.let { Json.of(wordDetailsMapper.jsonObjectMapper.writeValueAsString(it)) },
                        culturalNotes = request.culturalNotes,
                        learningTips = request.learningTips,
                        userId = userId
                    )

                    wordDetailsRepository.save(wordDetailsToSave)
                }
            }
            .map {
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(wordDetailsMapper.toDTO(it).toCompact())
            }
    }

    override fun updateWordDetails(
        wordId: UUID,
        request: UpdateWordDetailsRequest,
        userId: UUID
    ): Mono<ResponseEntity<WordDetailsCompactDTO>> {
        return wordRepository
            .findByIdAndUserId(
                id = wordId,
                userId = userId
            )
            .switchIfEmpty(
                Mono.error(NotFoundException("Word with id $wordId not found"))
            )
            .flatMap {
                wordDetailsService.getWordDetailsByWordId(
                    wordId = wordId,
                    userId = userId
                )
            }
            .flatMap { existing ->
                val updated = existing.copy(
                    useCases = if (request.useCases.isPresent) request.useCases.get() else existing.useCases,
                    synonyms = if (request.synonyms.isPresent) request.synonyms.get() else existing.synonyms,
                    antonyms = if (request.antonyms.isPresent) request.antonyms.get() else existing.antonyms,
                    commonMistakes = if (request.commonMistakes.isPresent) request.commonMistakes.get() else existing.commonMistakes,
                    exampleSentences = if (request.exampleSentences.isPresent) request.exampleSentences.get() else existing.exampleSentences,
                    collocations = if (request.collocations.isPresent) request.collocations.get() else existing.collocations,
                    pronunciation = if (request.pronunciation.isPresent) request.pronunciation.get() else existing.pronunciation,
                    grammar = if (request.grammar.isPresent) request.grammar.get() else existing.grammar,
                    culturalNotes = if (request.culturalNotes.isPresent) request.culturalNotes.get() else existing.culturalNotes,
                    learningTips = if (request.learningTips.isPresent) request.learningTips.get() else existing.learningTips,
                )

                wordDetailsRepository.save(
                    wordDetailsMapper.toEntity(updated)
                )
            }
            .map {
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(wordDetailsMapper.toDTO(it).toCompact())
            }
    }
}