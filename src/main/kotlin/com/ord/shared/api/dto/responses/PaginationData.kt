package com.ord.shared.api.dto.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Pagination metadata for paginated responses")
data class PaginationData(
    @Schema(
        description = "Current page number (0-indexed)",
        example = "0"
    )
    val page: Int,

    @Schema(
        description = "Number of items per page",
        example = "20"
    )
    val perPage: Int,

    @Schema(
        description = "Total number of results across all pages",
        example = "150"
    )
    val totalResults: Long,

    @Schema(
        description = "Number of results on the current page",
        example = "20"
    )
    val resultsOnCurrentPage: Int
) {
    val totalPages: Int
        @Schema(
            description = "Total number of pages",
            example = "8"
        )
        get() = if (perPage == 0) 0 else (totalResults / perPage).toInt() + if (totalResults % perPage == 0L) 0 else 1
}