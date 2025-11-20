package com.ord.shared.domain.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class SortDirection {
    ASC,
    DESC
}

fun SortDirection.isDesc(): Boolean {
    return this == SortDirection.DESC
}
