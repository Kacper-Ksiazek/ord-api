package com.ord.core.word.api.crud.responses.dto

import com.ord.shared.api.dto.responses.PaginationData
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Paginated words response")
data class WordsPaginatedDataResponse(
    val pagination: PaginationData,
    val data: List<WordListItem>,
)
