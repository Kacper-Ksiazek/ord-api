package com.backend.ord.core.word.api.requests.enums

enum class GetAllWordsSortOptions {
    CREATED_AT,
    ORIGIN,
    BOOKMARKED,
}

fun GetAllWordsSortOptions.toSQLColumnName(): String {
    return when (this) {
        GetAllWordsSortOptions.CREATED_AT -> "createdAt"
        GetAllWordsSortOptions.ORIGIN -> "origin"
        GetAllWordsSortOptions.BOOKMARKED -> "isBookmarked"
    }
}
