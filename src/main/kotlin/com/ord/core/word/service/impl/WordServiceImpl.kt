package com.ord.core.word.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.api.requests.enums.toggleProperty
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.WordMapper
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.repository.WordRepository
import com.ord.core.word.service.WordService
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.service.UserActivityLogService
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class WordServiceImpl(
    private val wordRepository: WordRepository,
    val wordMapper: WordMapper,
    val userActivityLogService: UserActivityLogService
) : WordService {
    override val repository: WordRepository = wordRepository

    override fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Mono<Int> {
        return repository.changeBankForSingleWord(
            bankId = bankId,
            wordId = wordId,
            userId = userId
        ).flatMap { result ->
            if (result == 0) {
                Mono.error(NotFoundException("Word with id $wordId for user with id $userId not found"))
            } else {
                Mono.just(result)
            }
        }
    }

    override fun changeBankForMultipleWords(
        wordIds: List<UUID>,
        bankId: UUID?,
        userId: UUID
    ): Mono<Int> {
        return repository.changeBankForMultipleWords(
            bankId = bankId,
            wordIds = wordIds,
            userId = userId
        ).flatMap { result ->
            when {
                result == 0 -> Mono.error(NotFoundException("No words found for user with id $userId"))
                else -> Mono.just(result)
            }
        }
    }

    override fun getWordsForPromptGeneration(
        language: LanguageName,
        amountOfLatestWord: Int,
        amountOfProblematicWord: Int
    ): Mono<Set<String>> {
        val latestWords = repository.findNOfLatestWords(
            language = language,
            limit = amountOfLatestWord
        ).collectList()

        val problematicWords = repository.findNOfMostDifficultWords(
            language = language,
            limit = amountOfProblematicWord
        ).collectList()

        return Mono.zip(latestWords, problematicWords) { latest, problematic ->
            (latest + problematic).toSet()
        }
    }

    override fun getWordsForPromptGeneration(
        language: LanguageName,
        banksIds: List<UUID>
    ): Mono<Set<String>> {
        return repository.findAllWordsFromBanks(
            language = language,
            banksIds = banksIds
        ).collectList().map { it.toSet() }
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

        userId: UUID,

        page: Int,
        perPage: Int
    ): Mono<PaginatedDataResponse<WordListItem>> {
        return repository.findManyWords(
            userId = userId,
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

            page = page,
            perPage = perPage
        )
    }

    override fun findOneWord(
        wordId: UUID,
        userId: UUID
    ): Mono<SingleWordResponse> {
        return repository.findOneWord(
            wordId = wordId,
            userId = userId
        )
    }

    override fun toggleProperty(
        wordId: UUID,
        userId: UUID,
        property: WordToggleableProperty
    ): Mono<WordEntity> {
        return repository.findByIdAndUserId(id = wordId, userId = userId)
            .switchIfEmpty(Mono.error(NotFoundException("Word with id $wordId not found")))
            .map { it!! }
            .map { wordEntity -> wordEntity.toggleProperty(property) }
            .flatMap { updatedEntity -> repository.save(updatedEntity) }
    }

    override fun togglePropertyForManyWords(
        wordIds: Set<UUID>,
        userId: UUID,
        property: WordToggleableProperty
    ): Flux<WordEntity> {
        return repository.findAllByIdInAndUserId(ids = wordIds, userId = userId)
            .collectList()
            .flatMap { words ->
                if (words.isEmpty()) {
                    Mono.error(NotFoundException("No requested words found for user with id $userId"))
                } else {
                    repository.saveAll(
                        words.map { it.toggleProperty(property) }
                    ).collectList()
                }
            }
            .flatMapMany { Flux.fromIterable(it) }
    }

    override fun saveNewWord(
        word: WordEntity,
        userId: UUID,
    ): Mono<WordDTO> {
        val language = word.translatedFrom

        return repository.save(word)
            .flatMap { savedEntity ->
                countCreated(language = language, userId = userId)
                    .map { countingSummary ->
                        val userActivityLogsToSaveEntity: MutableSet<UserActivityLogEntity> = mutableSetOf()

                        if (countingSummary.today >= 10) {
                            userActivityLogsToSaveEntity.add(
                                UserActivityLogEntity(
                                    userId = userId,
                                    type = UserActivityType.WORDS_ADDED_IN_ONE_DAY_10,
                                    language = language,
                                )
                            )
                        }

                        if (countingSummary.week >= 50) {
                            userActivityLogsToSaveEntity.add(
                                UserActivityLogEntity(
                                    userId = userId,
                                    type = UserActivityType.WORDS_ADDED_IN_ONE_WEEK_50,
                                    language = language,
                                )
                            )
                        }

                        wordMapper.toDTO(savedEntity)
                    }
            }
    }

    override fun countCreated(
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary> {
        return repository.countCreated(language = language, userId = userId)
    }

    override fun countCompleted(
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary> {
        return repository.countCompleted(language = language, userId = userId)
    }
}