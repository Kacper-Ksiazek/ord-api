package com.ord.core.word.api.crud.requests.enums

import com.ord.core.word.models.word.WordEntity
import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class WordToggleableProperty {
    IS_BOOKMARKED,
}

fun WordEntity.toggleProperty(property: WordToggleableProperty): WordEntity {
    when (property) {
        WordToggleableProperty.IS_BOOKMARKED -> isBookmarked = !isBookmarked
    }

    return this
}