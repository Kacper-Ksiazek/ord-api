package com.ord.features.quickly_added_words.api.requests

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import jakarta.validation.constraints.Size

data class UpdateQAWRequest(
    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    val updatedWord: String? = null,

    @field:Size(max = 255, message = "Translation must not exceed 255 characters")
    val translation: String? = null,

    @field:Size(max = 2000, message = "Definition must not exceed 2000 characters")
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)