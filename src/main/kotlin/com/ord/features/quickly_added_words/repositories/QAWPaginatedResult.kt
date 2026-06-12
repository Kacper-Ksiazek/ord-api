package com.ord.features.quickly_added_words.repositories

import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.shared.api.dto.responses.PaginatedDataResponse

data class QAWPaginatedResult(
    val paginated: PaginatedDataResponse<QuicklyAddedWordEntity>,
    val unapprovedCount: Long?,
)
