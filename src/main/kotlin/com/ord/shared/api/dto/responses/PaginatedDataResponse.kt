package com.ord.shared.api.dto.responses

data class PaginatedDataResponse<T>(
    val pagination: PaginationData,
    val data: List<T>
)