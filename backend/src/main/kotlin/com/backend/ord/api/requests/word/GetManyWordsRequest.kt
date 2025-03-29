package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import java.util.*


interface GetManyWordsRequest {
    val language: LanguageName

    val page: Int?
    val perPage: Int?

    val wordType: WordType?
    val completed: Boolean?
    val bookmarked: Boolean?
    val searchingPhrase: String?
    val wordExtraMark: WordExtraMark?

    val banksIds: List<UUID>?
    val bankGroupsIds: List<UUID>?

    val sortDirection: SortDirection?
    val sortBy: GetAllWordsSortOptions?
}