package com.ord.core.word.api.facades.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.facades.WordCRUDFacade
import com.ord.core.word.api.facades.internal.getBankFromRequestOrNull
import com.ord.core.word.api.requests.dto.CreateWordRequest
import com.ord.core.word.api.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.requests.dto.UpdateWordRequest
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.WordMapper
import com.ord.core.word.service.WordService
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.service.BankService
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.extensions.convertToSetExplicitly
import io.r2dbc.postgresql.codec.Json
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordCRUDFacadeImpl(
    private val bankService: BankService,
    private val wordMapper: WordMapper,
    private val wordService: WordService,
) : WordCRUDFacade {
    private val objectMapper = jacksonObjectMapper()
    override fun getManyWords(
        requestBody: GetManyWordsRequest,
        userId: UUID
    ): Mono<ResponseEntity<PaginatedDataResponse<WordListItem>>> {
        return wordService
            .findManyWords(
                language = requestBody.language,
                wordType = requestBody.wordType,
                completed = requestBody.completed,
                wordExtraMark = requestBody.wordExtraMark,
                bookmarked = requestBody.bookmarked,
                searchingPhrase = requestBody.searchingPhrase,

                banksIds = requestBody.banksIds?.convertToSetExplicitly(paramName = "banksIds"),
                bankGroupsIds = requestBody.bankGroupsIds?.convertToSetExplicitly(paramName = "bankGroupsIds"),

                sortDirection = requestBody.sortDirection,
                sortBy = requestBody.sortBy,

                userId = userId,

                page = requestBody.page ?: 0,
                perPage = requestBody.perPage ?: 10
            )
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }


    override fun getSingleWord(
        id: UUID,
        userId: UUID,
    ): Mono<ResponseEntity<SingleWordResponse>> {
        return wordService
            .findOneWord(
                wordId = id,
                userId = userId,
            )
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }

    }

    override fun createWord(
        body: CreateWordRequest,
        user: UserDTO
    ): Mono<ResponseEntity<WordDTO>> {
        return getBankFromRequestOrNull(
            bankService = bankService,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            userId = user.id,
        )
            .flatMap { bank ->
                val wordToSave = WordEntity(
                    origin = body.origin,
                    translatedTo = body.translatedTo ?: user.nativeLanguage!!,
                    translatedFrom = body.translatedFrom,
                    type = body.type,
                    exampleSentences = Json.of(objectMapper.writeValueAsString(body.exampleSentences)),
                    translation = body.translation,
                    extraMark = body.extraMark,
                    definition = body.definition,
                    useCases = Json.of(objectMapper.writeValueAsString(body.useCases)),

                    userId = user.id,
                    bankId = bank.value?.id
                )

                wordService.saveNewWord(
                    word = wordToSave,
                    userId = user.id,
                )
            }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    }

    override fun updateWord(
        id: UUID,
        body: UpdateWordRequest,
        userId: UUID,
    ): Mono<ResponseEntity<WordDTO>> {
        return wordService
            .findByIdOrFail(
                id = id,
                userId = userId
            )
            .flatMap { currentWord ->
                getBankFromRequestOrNull(
                    bankService = bankService,
                    bankId = body.bankId,
                    bankToCreate = body.bankToCreate,
                    userId = userId
                ).map { bank ->
                    currentWord.copy(
                        origin = body.origin ?: currentWord.origin,
                        translatedTo = body.translatedTo ?: currentWord.translatedTo,
                        translatedFrom = body.translatedFrom ?: currentWord.translatedFrom,
                        type = body.type ?: currentWord.type,
                        translation = body.translation ?: currentWord.translation,
                        extraMark = body.extraMark ?: currentWord.extraMark,
                        definition = body.definition ?: currentWord.definition,

                        exampleSentences = body.exampleSentences?.let { Json.of(objectMapper.writeValueAsString(it)) }
                            ?: currentWord.exampleSentences,
                        useCases = body.useCases?.let { Json.of(objectMapper.writeValueAsString(it)) }
                            ?: currentWord.useCases,

                        bankId = bank.value?.id ?: currentWord.bankId
                    )
                }
            }
            .flatMap { updatedEntity -> wordService.save(updatedEntity) }
            .map { savedEntity ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(wordMapper.toDTO(savedEntity))
            }

    }

    override fun deleteWord(
        id: UUID,
        userId: UUID,
    ): Mono<ResponseEntity<Unit>> {
        return wordService
            .deleteById(
                id = id,
                userId = userId
            )
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build() })

    }
}