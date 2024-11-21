package com.backend.ord.repositories

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.SingleWordResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import java.util.*


interface WordRepositoryCustomMethods {
    fun findOneWord(
        wordId: UUID,
        user: User
    ): SingleWordResponse

    fun findManyWords(
        searchingPhrase: String?,
        bookmarkedOnly: Boolean?,

        banksIds: List<UUID>?,
        bankGroupsIds: List<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions,

        user: User,

        page: Int,
        perPage: Int
    ): PaginatedDataResponse<WordAsGetManyWordResponse>
}