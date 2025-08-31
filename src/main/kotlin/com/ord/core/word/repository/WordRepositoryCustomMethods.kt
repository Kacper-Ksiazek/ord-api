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
import reactor.core.publisher.Mono
import java.util.*

interface WordRepositoryCustomMethods {
    fun findOneWord(
        wordId: UUID,
        user: UserEntity
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
}