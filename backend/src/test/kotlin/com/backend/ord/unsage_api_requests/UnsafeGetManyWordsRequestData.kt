package com.backend.ord.unsage_api_requests

data class UnsafeGetManyWordsRequestData(
    val language: Any?,

    val page: Any?,
    val perPage: Any?,

    val wordType: Any?,
    val completed: Any?,
    val wordExtraMark: Any?,
    val bookmarked: Any?,
    val searchingPhrase: Any?,

    val banksIds: Any?,
    val bankGroupsIds: Any?,

    val sortDirection: Any?,
    val sortBy: Any?
)