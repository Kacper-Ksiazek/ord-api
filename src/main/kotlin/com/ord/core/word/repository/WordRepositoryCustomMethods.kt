package com.ord.core.word.repository

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface WordRepositoryCustomMethods {
    // ------
    // READ
    // ------

    fun findOneWord(
        wordId: UUID,
        userId: UUID,
    ): Mono<SingleWordResponse>

    fun findManyWords(
        userId: UUID,
        language: LanguageName,

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
        perPage: Int
    ): Mono<PaginatedDataResponse<WordListItem>>


    fun findNOfLatestWords(
        userId: UUID,
        language: LanguageName,
        limit: Int
    ): Flux<String>


    fun findNOfMostDifficultWords(
        userId: UUID,
        language: LanguageName,
        limit: Int
    ): Flux<String>


    fun findAllWordsFromBanks(
        userId: UUID,
        language: LanguageName,
        banksIds: List<UUID>
    ): Flux<String>


    fun findAllWordByTheirOrigins(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID
    ): Flux<WordEntity>


    fun getWordsForGame(
        userId: UUID,
        language: LanguageName,
        completed: Boolean,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
    ): Mono<Set<String>>

    // ------
    // AGGREGATE
    // ------

    fun countCreated(
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary>


    fun countCompleted(
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary>

    // ------
    // UPDATE
    // ------

    fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Mono<Int>


    fun changeBankForMultipleWords(
        bankId: UUID?,
        wordIds: List<UUID>,
        userId: UUID
    ): Mono<Int>
}