package com.backend.ord.shared.enums

enum class SortDirection {
    ASC,
    DESC
}

fun SortDirection.isDesc(): Boolean {
    return this == SortDirection.DESC
}
