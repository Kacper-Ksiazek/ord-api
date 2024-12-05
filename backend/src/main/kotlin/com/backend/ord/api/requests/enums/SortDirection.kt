package com.backend.ord.api.requests.enums

fun SortDirection.isDesc(): Boolean {
    return this == SortDirection.DESC
}

enum class SortDirection {
    ASC,
    DESC
}