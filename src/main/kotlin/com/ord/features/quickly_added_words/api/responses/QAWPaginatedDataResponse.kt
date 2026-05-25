package com.ord.features.quickly_added_words.api.responses

import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
import com.ord.shared.api.dto.responses.PaginationData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Paginated quickly added words response with unapproved count metadata")
data class QAWPaginatedDataResponse(
    @Schema(description = "Pagination metadata")
    val pagination: PaginationData,

    @Schema(description = "Array of quickly added words for the current page")
    val data: List<QuicklyAddedWordDTO>,

    @Schema(
        description = "Total number of unapproved quickly added words for the user. Always present when isApproved filter is not applied.",
        example = "3"
    )
    val unapprovedCount: Long?,
)
