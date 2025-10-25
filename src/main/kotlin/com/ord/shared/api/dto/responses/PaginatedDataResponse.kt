package com.ord.shared.api.dto.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Generic paginated response wrapper for list endpoints")
data class PaginatedDataResponse<T>(
    @Schema(description = "Pagination metadata")
    val pagination: PaginationData,

    @Schema(description = "Array of results for the current page")
    val data: List<T>
)