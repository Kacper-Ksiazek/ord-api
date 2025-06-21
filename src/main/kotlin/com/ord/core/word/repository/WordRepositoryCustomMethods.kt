package com.ord.core.word.repository

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.domain.enums.SortDirection
import java.util.*

interface WordRepositoryCustomMethods {
    fun findOneWord(
        wordId: UUID,
        user: UserEntity
    ): SingleWordResponse

    fun findManyWords(
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,

        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions,

        user: UserEntity,

        page: Int,
        perPage: Int
    ): PaginatedDataResponse<WordListItem>
}