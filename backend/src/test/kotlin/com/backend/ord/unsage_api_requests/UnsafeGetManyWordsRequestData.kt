package com.backend.ord.unsage_api_requests

data class UnsafeGetManyWordsRequestData(
    val language: Any?,

    val page: Any?,
    val perPage: Any?,

    val wordType: Any?,
    val wordExtraMark: Any?,
    val bookmarkedOnly: Any?,
    val searchingPhrase: Any?,

    val banksIds: Any?,
    val bankGroupsIds: Any?,

    val sortDirection: Any?,
    val sortBy: Any?
)