package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
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