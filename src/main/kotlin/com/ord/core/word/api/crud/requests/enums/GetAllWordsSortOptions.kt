package com.ord.core.word.api.crud.requests.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class GetAllWordsSortOptions(val column: String) {
    CREATED_AT("created_at"),
    SOURCE_WORD("source_word"),
}
