package com.backend.ord.api.requests.word.enums

fun GetAllWordsSortOptions.toSQLColumnName(): String {
    return when (this) {
        GetAllWordsSortOptions.CREATED_AT -> "createdAt"
        GetAllWordsSortOptions.ORIGIN -> "origin"
        GetAllWordsSortOptions.BOOKMARKED -> "isBookmarked"
    }
}

enum class GetAllWordsSortOptions {
    CREATED_AT,
    ORIGIN,
    BOOKMARKED,
}