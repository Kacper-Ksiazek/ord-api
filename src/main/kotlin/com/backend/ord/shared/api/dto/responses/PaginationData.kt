package com.backend.ord.shared.api.dto.responses

data class PaginationData(
    val page: Int,
    val perPage: Int,
    val totalRecords: Long,
    val recordsOnCurrentPage: Int
) {
    val totalPages: Int
        get() = if (perPage == 0) 0 else (totalRecords / perPage).toInt() + if (totalRecords % perPage == 0L) 0 else 1
}