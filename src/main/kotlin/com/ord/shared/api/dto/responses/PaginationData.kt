package com.ord.shared.api.dto.responses

data class PaginationData(
    val page: Int,
    val perPage: Int,
    val totalResults: Long,
    val resultsOnCurrentPage: Int
) {
    val totalPages: Int
        get() = if (perPage == 0) 0 else (totalResults / perPage).toInt() + if (totalResults % perPage == 0L) 0 else 1
}