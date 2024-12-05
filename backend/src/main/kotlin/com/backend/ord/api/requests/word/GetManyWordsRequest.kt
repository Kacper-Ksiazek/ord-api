package com.backend.ord.api.requests.word

import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions

import java.util.UUID


interface GetManyWordsRequest {
    val language: LanguageName

    val page: Int?
    val perPage: Int?

    val wordType: WordType?
    val searchingPhrase: String?
    val wordExtraMark: WordExtraMark?
    val bookmarkedOnly: Boolean?

    val banksIds: List<UUID>?
    val bankGroupsIds: List<UUID>?

    val sortDirection: SortDirection?
    val sortBy: GetAllWordsSortOptions?
}