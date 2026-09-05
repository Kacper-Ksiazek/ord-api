package com.ord.core.word.api.crud.facades.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.crud.facades.WordCRUDFacade
import com.ord.core.word.api.crud.facades.internal.getBankFromRequestOrNull
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.crud.requests.dto.UpdateWordRequest
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.WordMapper
import com.ord.core.word.services.WordService
import com.ord.core.word.models.word_details.toCompact
import com.ord.core.word.services.WordDetailsService
import com.ord.features.bank.service.BankService
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
    override fun listWords(
        userId: UUID,
        language: LanguageName,
        page: Int?,
        perPage: Int?,
        isFromUnverifiedSource: Boolean?,
        hasProgress: Boolean?,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>> {
        return wordService
            .findManyWords(
                language = language,
                isFromUnverifiedSource = isFromUnverifiedSource,
                hasProgress = hasProgress,
                userId = userId,
                page = page ?: 0,
                perPage = perPage ?: 50,
                includeUnverifiedSourceCount = hasProgress == null && isFromUnverifiedSource == null,
            )
            .map { result ->
                WordsPaginatedDataResponse(
                    pagination = result.paginated.pagination,
                    data = result.paginated.data,
                    unverifiedSourceCount = result.unverifiedSourceCount,
                )
            }
            .map { ResponseEntity.ok(it) }
    }

    override fun searchWords(
        requestBody: GetManyWordsRequest,
        userId: UUID,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>> {
        return wordService
            .findManyWords(
                language = requestBody.language,
                wordType = requestBody.wordType,
                isFromUnverifiedSource = requestBody.isFromUnverifiedSource,
                hasProgress = requestBody.hasProgress ?: true,
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
                perPage = requestBody.perPage ?: 10,
            )
            .map { result ->
                WordsPaginatedDataResponse(
                    pagination = result.paginated.pagination,
                    data = result.paginated.data,
                    unverifiedSourceCount = result.unverifiedSourceCount,
                )
            }
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    override fun getSingleWord(id: UUID, userId: UUID): Mono<ResponseEntity<SingleWordResponse>> {
        return wordService
            .findOneWord(wordId = id, userId = userId)
            .flatMap { word ->
                wordDetailsService
                    .getWordDetailsByWordId(wordId = id, userId = userId)
                    .map { details -> word.copy(details = details.toCompact()) }
                    .onErrorResume { Mono.just(word.copy(details = null)) }
            }
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    override fun createWord(body: CreateWordRequest, user: UserDTO): Mono<ResponseEntity<WordDTO>> {
        return getBankFromRequestOrNull(
            bankService = bankService,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            userId = user.id,
        )
            .flatMap { bank ->
                val wordToSave = WordEntity(
                    type = body.type,
                    sourceWord = body.sourceWord,
                    translation = body.translation,
                    definition = body.definition,
                    extraMark = body.extraMark,
                    language = body.language,
                    userId = user.id,
                    bankId = bank.value?.id,
                )
                wordService.saveNewActiveWord(word = wordToSave, userId = user.id)
            }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    override fun updateWord(id: UUID, body: UpdateWordRequest, userId: UUID): Mono<ResponseEntity<WordDTO>> {
        return wordService
            .findByIdOrFail(id = id, userId = userId)
            .flatMap { currentWord ->
                getBankFromRequestOrNull(
                    bankService = bankService,
                    bankId = body.bankId,
                    bankToCreate = body.bankToCreate,
                    userId = userId,
                ).map { bank ->
                    currentWord.copy(
                        type = body.type ?: currentWord.type,
                        sourceWord = body.sourceWord ?: currentWord.sourceWord,
                        translation = body.translation ?: currentWord.translation,
                        definition = body.definition ?: currentWord.definition,
                        extraMark = body.extraMark ?: currentWord.extraMark,
                        language = body.language ?: currentWord.language,
                        bankId = bank.value?.id ?: currentWord.bankId,
                    )
                }
            }
            .flatMap { updatedEntity -> wordService.save(updatedEntity) }
            .flatMap { saved ->
                wordService.hasProgress(saved.id!!, userId).flatMap { hasProgress ->
                    if (hasProgress) {
                        wordService.findOneWord(saved.id!!, userId).map { response ->
                            wordMapper.toDTO(saved).apply { progress = response.progress }
                        }
                    } else {
                        Mono.just(wordMapper.toDTO(saved))
                    }
                }
            }
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    override fun deleteWord(id: UUID, userId: UUID): Mono<ResponseEntity<Unit>> {
        return wordService
            .deleteById(id = id, userId = userId)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build() })
    }
}
