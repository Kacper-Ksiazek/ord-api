package com.ord.core.word.api.details.facades.impl

import com.ord.core.word.api.details.facades.WordDetailsFacade
import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word_details.WordDetailsDTO
import com.ord.core.word.repositories.WordRepository
import com.ord.core.word.services.WordDetailsService
import com.ord.exceptions.REST.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordDetailsFacadeImpl(
    private val wordDetailsService: WordDetailsService,
    private val wordRepository: WordRepository
) : WordDetailsFacade {

    override fun createWordDetails(
        wordId: UUID,
        request: CreateWordDetailsRequest,
        userId: UUID
    ): Mono<ResponseEntity<WordDetailsDTO>> {
        return wordRepository
            .findByIdAndUserId(
                id = wordId,
                userId = userId
            )
            .switchIfEmpty(
                Mono.error(NotFoundException("Word with id $wordId not found"))
            )
            .flatMap {
                val wordDetailsDTO = WordDetailsDTO(
                    id = UUID.randomUUID(),
                    wordId = wordId,
                    useCases = request.useCases,
                    synonyms = request.synonyms,
                    antonyms = request.antonyms,
                    commonMistakes = request.commonMistakes,
                    exampleSentences = request.exampleSentences,
                    collocations = request.collocations,
                    pronunciation = request.pronunciation,
                    grammar = request.grammar,
                    culturalNotes = request.culturalNotes,
                    learningTips = request.learningTips,
                    userId = userId
                )

                wordDetailsService.createWordDetails(
                    wordId = wordId,
                    wordDetailsDTO = wordDetailsDTO,
                    userId = userId
                )
            }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    override fun updateWordDetails(
        wordId: UUID,
        request: UpdateWordDetailsRequest,
        userId: UUID
    ): Mono<ResponseEntity<WordDetailsDTO>> {
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
                val updated = WordDetailsDTO(
                    id = existing.id,
                    wordId = wordId,
                    useCases = request.useCases.orElse(existing.useCases),
                    synonyms = request.synonyms.orElse(existing.synonyms),
                    antonyms = request.antonyms.orElse(existing.antonyms),
                    commonMistakes = request.commonMistakes.orElse(existing.commonMistakes),
                    exampleSentences = request.exampleSentences.orElse(existing.exampleSentences),
                    collocations = request.collocations.orElse(existing.collocations),
                    pronunciation = request.pronunciation.orElse(existing.pronunciation),
                    grammar = request.grammar.orElse(existing.grammar),
                    culturalNotes = request.culturalNotes.orElse(existing.culturalNotes),
                    learningTips = request.learningTips.orElse(existing.learningTips),
                    userId = userId
                )

                wordDetailsService.updateWordDetails(
                    wordId = wordId,
                    wordDetailsDTO = updated,
                    userId = userId
                )
            }
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
}