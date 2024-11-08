package com.backend.ord.services

import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.services.bases.UserResourceService
import java.util.UUID
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType


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
        searchingPhrase: String?,
        bookmarkedOnly: Boolean?,

        banksIds: List<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection?,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions?,

        user: User,

        page: Int,
        perPage: Int
    ): List<WordAsGetManyWordResponse>
}