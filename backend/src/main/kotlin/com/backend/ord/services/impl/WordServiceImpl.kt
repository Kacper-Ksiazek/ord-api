package com.backend.ord.services.impl

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.SingleWordResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.repositories.WordRepository
import com.backend.ord.services.WordService
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class WordServiceImpl(
    override val repository: WordRepository
) : WordService {
    @Transactional
    override fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Int {
        return repository.changeBankForSingleWord(
            bankId = bankId,
            wordId = wordId,
            userId = userId
        ).let {
            if (it == 0) {
                throw NotFoundException("Word with id $wordId for user with id $userId not found")
            }
            it
        }
    }

    @Transactional
    override fun changeBankForMultipleWords(
        wordIds: List<UUID>,
        bankId: UUID?,
        userId: UUID
    ): Int {
        val words = repository.findAll()

        return repository.changeBankForMultipleWords(
            bankId = bankId,
            wordIds = wordIds,
            userId = userId
        ).let {
            if (it == 0) {
                throw NotFoundException("No words found for user with id $userId")
            } else if (it != wordIds.size) {
                throw NotFoundException("Not all words found for user with id $userId")
            }
            it
        }
    }

    override fun getWordsForPromptGeneration(
        language: LanguageName,
        amountOfLatestWord: Int,
        amountOfProblematicWord: Int
    ): Set<String> {
        val latestWords = repository.findNOfLatestWords(
            language = language,
            pageable = PageRequest.of(0, amountOfLatestWord)
        )

        val problematicWords = repository.findNOfMostDifficultWords(
            language = language,
            pageable = PageRequest.of(0, amountOfProblematicWord)
        )

        return (latestWords + problematicWords).toSet()
    }

    override fun getWordsForPromptGeneration(
        language: LanguageName,
        banksIds: List<UUID>
    ): Set<String> {
        return repository.findAllWordsFromBanks(
            language = language,
            banksIds = banksIds
        ).toSet()
    }

    override fun findManyWords(
        searchingPhrase: String?,
        bookmarkedOnly: Boolean?,

        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection?,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions?,

        user: User,

        page: Int,
        perPage: Int
    ): PaginatedDataResponse<WordAsGetManyWordResponse> {
        return repository.findManyWords(
            language = language,
            bookmarkedOnly = bookmarkedOnly,

            wordType = wordType,
            wordExtraMark = wordExtraMark,
            searchingPhrase = searchingPhrase,

            sortDirection = sortDirection ?: SortDirection.DESC,
            sortBy = sortBy ?: GetAllWordsSortOptions.CREATED_AT,

            banksIds = banksIds,
            bankGroupsIds = bankGroupsIds,

            user = user,

            page = page,
            perPage = perPage
        )
    }

    override fun findOneWord(
        wordId: UUID,
        user: User
    ): SingleWordResponse {
        return repository.findOneWord(
            wordId = wordId,
            user = user
        )
    }
}