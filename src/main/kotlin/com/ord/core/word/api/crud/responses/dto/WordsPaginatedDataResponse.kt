package com.ord.core.word.api.crud.responses.dto

import com.ord.shared.api.dto.responses.PaginationData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Paginated words response with optional unverified source count")
data class WordsPaginatedDataResponse(
    val pagination: PaginationData,
    val data: List<WordListItem>,

    @Schema(
        description = "Count of words from unverified sources when isFromUnverifiedSource filter is omitted",
        nullable = true,
        example = "12",
    )
    val unverifiedSourceCount: Long? = null,
)
