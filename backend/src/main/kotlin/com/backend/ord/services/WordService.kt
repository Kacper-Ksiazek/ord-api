package com.backend.ord.services

import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.services.bases.UserResourceService
import java.util.UUID
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.requests.word.enums.WordToggleableProperty
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.SingleWordResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType


interface WordService : UserResourceService<Word> {
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
        searchingPhrase: String? = null,
        bookmarkedOnly: Boolean? = null,
        // TODO: completedOnly: Boolean? = null,

        banksIds: Set<UUID>? = null,
        bankGroupsIds: Set<UUID>? = null,

        wordType: WordType? = null,
        language: LanguageName,
        sortDirection: SortDirection? = null,
        wordExtraMark: WordExtraMark? = null,
        sortBy: GetAllWordsSortOptions? = null,

        user: User,

        page: Int = 0,
        perPage: Int = 10
    ): PaginatedDataResponse<WordAsGetManyWordResponse>

    fun findOneWord(
        wordId: UUID,
        user: User
    ): SingleWordResponse

    fun toggleProperty(
        wordId: UUID,
        userId: UUID,
        property: WordToggleableProperty
    ): Word
}