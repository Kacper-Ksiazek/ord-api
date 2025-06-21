package com.ord.core.word.api.facades.impl

import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.UserMapper
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
import com.ord.features.bank.model.BankMapper
import com.ord.features.bank.service.BankService
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.extensions.convertToSetExplicitly
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordCRUDFacadeImpl(
    private val bankService: BankService,
    private val wordMapper: WordMapper,
    private val userMapper: UserMapper,
    private val wordService: WordService,
    private val bankMapper: BankMapper
) : WordCRUDFacade {
    override fun getManyWords(
        requestBody: GetManyWordsRequest,
        user: UserEntity
    ): PaginatedDataResponse<WordListItem> {
        return wordService.findManyWords(
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

            user = user,

            page = requestBody.page ?: 0,
            perPage = requestBody.perPage ?: 10
        )

    }

    override fun getSingleWord(
        id: UUID,
        user: UserEntity
    ): SingleWordResponse {
        return wordService.findOneWord(
            wordId = id,
            user = user
        )
    }

    override fun createWord(
        body: CreateWordRequest,
        user: UserEntity
    ): WordDTO {
        val bank = getBankFromRequestOrNull(
            bankService = bankService,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            user = user
        )

        val wordToSave = WordDTO(
            origin = body.origin,
            translatedTo = body.translatedTo ?: user.nativeLanguage,
            translatedFrom = body.translatedFrom,
            type = body.type,
            exampleSentences = body.exampleSentences,
            translation = body.translation,
            extraMark = body.extraMark,
            definition = body.definition,
            useCases = body.useCases,

            user = userMapper.toDTO(user),
            bank = bankMapper.toDTOOrNull(bank)
        )

        val result = wordService.save(wordMapper.toEntity(wordToSave))
        return wordMapper.toDTO(result)
    }

    override fun updateWord(
        id: UUID,
        body: UpdateWordRequest,
        user: UserEntity
    ): WordDTO {
        val currentWord = wordService.findByIdOrFail(id = id, userId = user.id)

        val bank = getBankFromRequestOrNull(
            bankService = bankService,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            user = user
        )

        val result: WordEntity = wordService.save(
            wordMapper.toEntity(
                WordDTO(
                    id = id,
                    origin = body.origin ?: currentWord.origin,
                    translatedTo = body.translatedTo ?: currentWord.translatedTo,
                    translatedFrom = body.translatedFrom ?: currentWord.translatedFrom,
                    type = body.type ?: currentWord.type,
                    exampleSentences = body.exampleSentences ?: currentWord.exampleSentences,
                    translation = body.translation ?: currentWord.translation,
                    extraMark = body.extraMark ?: currentWord.extraMark,
                    definition = body.definition ?: currentWord.definition,
                    useCases = body.useCases ?: currentWord.useCases,

                    user = userMapper.toDTO(user),
                    bank = bankMapper.toDTOOrNull(bank)
                )
            )
        )

        return wordMapper.toDTO(result)
    }

    override fun deleteWord(
        id: UUID,
        user: UserEntity
    ) {
        wordService.deleteById(
            id = id,
            userId = user.id
        )
    }
}