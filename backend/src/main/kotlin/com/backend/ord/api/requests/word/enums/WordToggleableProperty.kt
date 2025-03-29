package com.backend.ord.api.requests.word.enums

import com.backend.ord.domain.persistence.entities.Word

enum class WordToggleableProperty {
    IS_BOOKMARKED,
    IS_COMPLETED,
}

fun Word.toggleProperty(property: WordToggleableProperty): Word {
    when (property) {
        WordToggleableProperty.IS_BOOKMARKED -> isBookmarked = !isBookmarked
        WordToggleableProperty.IS_COMPLETED -> isCompleted = !isCompleted
    }

    return this
}