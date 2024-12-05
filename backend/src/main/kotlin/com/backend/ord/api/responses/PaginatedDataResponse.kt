package com.backend.ord.api.responses

data class PaginatedDataResponse<T>(
    val pagination: PaginationData,
    val data: List<T>
)