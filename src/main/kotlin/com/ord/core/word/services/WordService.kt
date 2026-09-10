package com.ord.core.word.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.crud.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.crud.requests.enums.WordToggleableProperty
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.repositories.WordOverviewCounts
import com.ord.core.word.repositories.WordsPaginatedResult
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface WordService : UserResourceService<WordEntity> {
    fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Mono<Int>
    fun changeBankForMultipleWords(wordIds: List<UUID>, bankId: UUID?, userId: UUID): Mono<Int>
    fun getWordsForPromptGeneration(
        userId: UUID,
        language: LanguageName,
        amountOfLatestWord: Int = 10,
        amountOfProblematicWord: Int = 10,
    ): Mono<Set<String>>
    fun getWordsForPromptGeneration(userId: UUID, language: LanguageName, banksIds: List<UUID>): Mono<Set<String>>
    fun getWordsForGame(
        userId: UUID,
        language: LanguageName,
        completed: Boolean,
        banksIds: Set<UUID>? = null,
        bankGroupsIds: Set<UUID>? = null,
    ): Mono<Set<String>>
    fun findManyWords(
        isFromUnverifiedSource: Boolean? = null,
        hasProgress: Boolean? = null,
        completed: Boolean? = null,
        searchingPhrase: String? = null,
        bookmarked: Boolean? = null,
        banksIds: Set<UUID>? = null,
        bankGroupsIds: Set<UUID>? = null,
        wordTypes: Set<WordType>? = null,
        language: LanguageName,
        sortDirection: SortDirection? = null,
        wordExtraMarks: Set<WordExtraMark>? = null,
        sortBy: GetAllWordsSortOptions? = null,
        userId: UUID,
        page: Int = 0,
        perPage: Int = 10,
        includeUnverifiedSourceCount: Boolean = false,
    ): Mono<WordsPaginatedResult>

    fun findOneWord(wordId: UUID, userId: UUID): Mono<SingleWordResponse>

    fun toggleProperty(wordId: UUID, userId: UUID, property: WordToggleableProperty): Mono<WordEntity>

    fun togglePropertyForManyWords(wordIds: Set<UUID>, userId: UUID, property: WordToggleableProperty): Flux<WordEntity>

    fun saveNewActiveWord(word: WordEntity, userId: UUID): Mono<WordDTO>

    fun captureWord(request: CaptureWordRequest, userId: UUID, isFromUnverifiedSource: Boolean = false): Mono<WordDTO>

    fun bulkCaptureWords(
        requests: List<CaptureWordRequest>,
        userId: UUID,
        isFromUnverifiedSource: Boolean = false,
    ): Mono<List<WordDTO>>

    fun activateWord(wordId: UUID, userId: UUID): Mono<WordDTO>

    fun activateManyWords(wordIds: Set<UUID>, userId: UUID): Mono<Unit>

    fun countOverview(userId: UUID, language: LanguageName? = null): Mono<WordOverviewCounts>

    fun countCreated(language: LanguageName, userId: UUID): Mono<CountingSummary>

    fun hasProgress(wordId: UUID, userId: UUID): Mono<Boolean>
}
