package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.GetManyWordsRequest
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class GetManyWordsRequestData(
    @field:NotNull(message = "Language is required")
    override val language: LanguageName,

    @field:Min(value = 0, message = "Page must be greater than or equal to 0")
    override val page: Int?,

    @field:Min(value = 1, message = "Per page must be greater than 0")
    @field:Max(value = 500, message = "Per page must be less than or equal to 500")
    override val perPage: Int?,

    @field:Size(min = 1, max = 64, message = "Searching phrase must be between 1 and 64 characters")
    override val searchingPhrase: String?,

    override val wordType: WordType?,
    override val wordExtraMark: WordExtraMark?,
    override val bookmarkedOnly: Boolean?,
    override val banksIds: List<UUID>?,
    override val bankGroupsIds: List<UUID>?,
    override val sortDirection: SortDirection?,
    override val sortBy: GetAllWordsSortOptions?
) : GetManyWordsRequest