package com.ord.core.word.api.crud.facades.impl

import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.crud.facades.WordCRUDFacade
import com.ord.core.word.api.crud.facades.internal.getBankFromRequestOrNull
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.crud.requests.dto.UpdateWordRequest
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.WordMapper
import com.ord.core.word.services.WordDetailsService
import com.ord.core.word.services.WordService
import com.ord.features.bank.service.BankService
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.extensions.convertToSetExplicitly
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordCRUDFacadeImpl(
    private val bankService: BankService,
    private val wordMapper: WordMapper,
    private val wordService: WordService,
    private val wordDetailsService: WordDetailsService,
) : WordCRUDFacade {
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
            .flatMap { word ->
                wordDetailsService
                    .getWordDetailsByWordId(
                        wordId = id,
                        userId = userId
                    )
                    .map { details -> word.copy(details = details) }
                    .onErrorResume { Mono.just(word.copy(details = null)) }
            }
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
                    type = body.type,
                    origin = body.origin,
                    translation = body.translation,
                    definition = body.definition,
                    extraMark = body.extraMark,

                    translatedFrom = body.translatedFrom,
                    translatedTo = body.translatedTo ?: user.nativeLanguage!!,

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
                        type = body.type ?: currentWord.type,
                        origin = body.origin ?: currentWord.origin,
                        translation = body.translation ?: currentWord.translation,
                        definition = body.definition ?: currentWord.definition,
                        extraMark = body.extraMark ?: currentWord.extraMark,

                        translatedFrom = body.translatedFrom ?: currentWord.translatedFrom,
                        translatedTo = body.translatedTo ?: currentWord.translatedTo,

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