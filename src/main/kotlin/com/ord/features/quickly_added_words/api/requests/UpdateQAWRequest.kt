package com.ord.features.quickly_added_words.api.requests

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType

data class UpdateQAWRequest(
    val updatedWord: String? = null,
    val definition: String? = null,
    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)