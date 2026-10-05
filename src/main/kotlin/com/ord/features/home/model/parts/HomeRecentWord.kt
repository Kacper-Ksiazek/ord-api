package com.ord.features.home.model.parts

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Word preview on the home screen. Definition is truncated.")
data class HomeRecentWord(
    val id: UUID,
    val sourceWord: String,
    val translation: String,

    @Schema(description = "Definition trimmed to 160 characters", nullable = true)
    val definitionPreview: String?,

    val isBookmarked: Boolean,
    val type: WordType,
    val extraMark: WordExtraMark?,
)
