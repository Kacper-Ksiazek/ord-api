package com.ord.core.word.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.services.UserResourceService
import java.util.*

interface WordService : UserResourceService<WordEntity> {
    fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Int

    fun changeBankForMultipleWords(
        wordIds: List<UUID>,
        bankId: UUID?,
        userId: UUID
    ): Int

    fun getWordsForPromptGeneration(
        language: LanguageName,
        amountOfLatestWord: Int = 10,
        amountOfProblematicWord: Int = 10
    ): Set<String>

    fun getWordsForPromptGeneration(
        language: LanguageName,
        banksIds: List<UUID>
    ): Set<String>

    fun findManyWords(
        completed: Boolean? = null,
        searchingPhrase: String? = null,
        bookmarked: Boolean? = null,

        banksIds: Set<UUID>? = null,
        bankGroupsIds: Set<UUID>? = null,

        wordType: WordType? = null,
        language: LanguageName,
        sortDirection: SortDirection? = null,
        wordExtraMark: WordExtraMark? = null,
        sortBy: GetAllWordsSortOptions? = null,

        user: UserEntity,

        page: Int = 0,
        perPage: Int = 10
    ): PaginatedDataResponse<WordListItem>

    fun findOneWord(
        wordId: UUID,
        user: UserEntity
    ): SingleWordResponse

    fun toggleProperty(
        wordId: UUID,
        userId: UUID,
        property: WordToggleableProperty
    ): WordEntity

    fun togglePropertyForManyWords(
        wordIds: Set<UUID>,
        userId: UUID,
        property: WordToggleableProperty
    ): List<WordEntity>

    fun saveNewWord(
        word: WordDTO,
        user: UserEntity
    ): WordDTO

    fun countCreated(language: LanguageName, userId: UUID): CountingSummary

    fun countCompleted(language: LanguageName, userId: UUID): CountingSummary
}