package com.ord.core.word.api.requests.enums

// TODO: Squash it

enum class GetAllWordsSortOptions(val column: String) {
    CREATED_AT("created_at"),
    ORIGIN("origin"),
    BOOKMARKED("is_bookmarked");
}
