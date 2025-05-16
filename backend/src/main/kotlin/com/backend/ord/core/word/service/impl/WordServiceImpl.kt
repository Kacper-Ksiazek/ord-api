package com.backend.ord.core.word.service.impl

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.requests.word.enums.WordToggleableProperty
import com.backend.ord.api.requests.word.enums.toggleProperty
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.SingleWordResponse
import com.backend.ord.api.responses.words.WordListItem
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.model.Word
import com.backend.ord.core.word.model.WordDTO
import com.backend.ord.core.word.model.WordMapper
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.repository.WordRepository
import com.backend.ord.core.word.service.WordService
import com.backend.ord.domain.infrastructure.CountingSummary
import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.services.UserActivityLogService
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class WordServiceImpl(
    override val repository: WordRepository,

    val wordMapper: WordMapper,
    val userActivityLogService: UserActivityLogService
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
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,

        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection?,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions?,

        user: UserEntity,

        page: Int,
        perPage: Int
    ): PaginatedDataResponse<WordListItem> {
        return repository.findManyWords(
            language = language,
            completed = completed,
            bookmarked = bookmarked,

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
        user: UserEntity,
    ): SingleWordResponse {
        return repository.findOneWord(
            wordId = wordId,
            user = user
        )
    }

    override fun toggleProperty(
        wordId: UUID,
        userId: UUID,
        property: WordToggleableProperty
    ): Word {
        val word: Word = repository.findOneForUser(id = wordId, userId = userId)
            ?: throw NotFoundException("Word with id $wordId not found")

        return repository.save(
            word.toggleProperty(property)
        )
    }

    override fun togglePropertyForManyWords(
        wordIds: Set<UUID>,
        userId: UUID,
        property: WordToggleableProperty
    ): List<Word> {
        val words = repository.findAllForUser(ids = wordIds, userId = userId)

        // Handle partial save
        if (words.isEmpty()) {
            throw NotFoundException("No requested words found for user with id $userId")
        }

        return repository.saveAll(
            words.map {
                it.toggleProperty(property)
            }
        )
    }

    override fun saveNewWord(
        word: WordDTO,
        user: UserEntity
    ): WordDTO {
        val result = repository.save(wordMapper.toEntity(word))
        val language = word.translatedFrom

        val userActivityLogsToSave: MutableSet<UserActivityLog> = mutableSetOf()

        countCreated(language = language, userId = user.id).let {
            if (it.today >= 10) {
                userActivityLogsToSave.add(
                    UserActivityLog(
                        user = user,
                        type = UserActivityType.WORDS_ADDED_IN_ONE_DAY_10,
                        language = language,
                    )
                )
            }

            if (it.week >= 50) {
                userActivityLogsToSave.add(
                    UserActivityLog(
                        user = user,
                        type = UserActivityType.WORDS_ADDED_IN_ONE_WEEK_50,
                        language = language,
                    )
                )
            }
        }

        return wordMapper.toDTO(result)
    }

    override fun countCreated(
        language: LanguageName,
        userId: UUID
    ): CountingSummary {
        return CountingSummary(
            repository.countCreated(language = language, userId = userId)
        )
    }

    override fun countCompleted(
        language: LanguageName,
        userId: UUID
    ): CountingSummary {
        return CountingSummary(
            repository.countCompleted(language = language, userId = userId)
        )
    }
}