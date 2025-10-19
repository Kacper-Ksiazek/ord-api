package com.ord.core.word.api.crud.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.core.word.api.crud.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.domain.enums.SortDirection
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class GetManyWordsRequest(
    @field:NotNull(message = "Language is required")
    @field:ValidLanguageName
    val language: LanguageName,

    @field:Min(value = 0, message = "Page must be greater than or equal to 0")
    val page: Int?,

    @field:Min(value = 1, message = "Per page must be greater than 0")
    @field:Max(value = 500, message = "Per page must be less than or equal to 500")
    val perPage: Int?,

    @field:Size(min = 1, max = 64, message = "Searching phrase must be between 1 and 64 characters")
    val searchingPhrase: String?,

    val wordType: WordType?,
    val wordExtraMark: WordExtraMark?,
    val bookmarked: Boolean?,
    val completed: Boolean?,
    val banksIds: List<UUID>?,
    val bankGroupsIds: List<UUID>?,
    val sortDirection: SortDirection?,
    val sortBy: GetAllWordsSortOptions?
)


data class UnsafeGetManyWordsRequest(
    val language: Any?,

    val page: Any?,
    val perPage: Any?,

    val wordType: Any?,
    val completed: Any?,
    val wordExtraMark: Any?,
    val bookmarked: Any?,
    val searchingPhrase: Any?,

    val banksIds: Any?,
    val bankGroupsIds: Any?,

    val sortDirection: Any?,
    val sortBy: Any?
)
