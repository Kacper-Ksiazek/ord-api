package com.ord.core.word.api.requests.enums

import com.ord.core.word.model.WordEntity

enum class WordToggleableProperty {
    IS_BOOKMARKED,
    IS_COMPLETED,
}

fun WordEntity.toggleProperty(property: WordToggleableProperty): WordEntity {
    when (property) {
        WordToggleableProperty.IS_BOOKMARKED -> isBookmarked = !isBookmarked
        WordToggleableProperty.IS_COMPLETED -> isCompleted = !isCompleted
    }

    return this
}