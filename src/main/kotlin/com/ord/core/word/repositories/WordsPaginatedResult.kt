package com.ord.core.word.repositories

import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.shared.api.dto.responses.PaginatedDataResponse

data class WordsPaginatedResult(
    val paginated: PaginatedDataResponse<WordListItem>,
    val capturedCount: Long?,
)
