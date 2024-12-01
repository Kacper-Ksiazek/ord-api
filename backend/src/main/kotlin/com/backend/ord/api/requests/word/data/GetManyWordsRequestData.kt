package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.GetManyWordsRequest
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class GetManyWordsRequestData(
    @field:NotBlank(message = "Language is required")
    override val language: LanguageName,

    @field:Size(min = 0, message = "Page must be greater than or equal to 0")
    override val page: Int?,

    @field:Size(min = 10, max = 500, message = "Per page must be between 10 and 500")
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