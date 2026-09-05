package com.ord.core.word.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.crud.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface WordRepositoryCustomMethods {
    fun findOneWord(
        wordId: UUID,
        userId: UUID,
    ): Mono<SingleWordResponse>

    fun findManyWords(
        userId: UUID,
        language: LanguageName,
        isFromUnverifiedSource: Boolean?,
        hasProgress: Boolean?,
        completed: Boolean?,
        bookmarked: Boolean?,
        searchingPhrase: String?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?,
        sortDirection: SortDirection,
        sortBy: GetAllWordsSortOptions,
        page: Int,
        perPage: Int,
        includeUnverifiedSourceCount: Boolean = false,
    ): Mono<WordsPaginatedResult>

    fun countOverview(userId: UUID): Mono<WordOverviewCounts>

    fun findNOfLatestWords(
        userId: UUID,
        language: LanguageName,
        limit: Int,
    ): Flux<String>

    fun findNOfMostDifficultWords(
        userId: UUID,
        language: LanguageName,
        limit: Int,
    ): Flux<String>

    fun findAllWordsFromBanks(
        userId: UUID,
        language: LanguageName,
        banksIds: List<UUID>,
    ): Flux<String>

    fun findAllSourceWordsByUserIdAndLanguage(
        userId: UUID,
        language: LanguageName,
    ): Flux<String>

    fun findAllWordByTheirOrigins(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID,
    ): Flux<WordEntity>

    fun getWordsForGame(
        userId: UUID,
        language: LanguageName,
        completed: Boolean,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
    ): Mono<Set<String>>

    fun countCreated(
        language: LanguageName,
        userId: UUID,
    ): Mono<CountingSummary>

    fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID,
    ): Mono<Int>

    fun changeBankForMultipleWords(
        bankId: UUID?,
        wordIds: List<UUID>,
        userId: UUID,
    ): Mono<Int>
}
