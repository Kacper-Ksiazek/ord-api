package com.ord.core.word.api.ai.responses.dto

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "AI-enriched fields for each requested word")
data class WordFillGapsResponse(
    val items: List<WordFillGapsResultItem>,
)

@Schema(description = "AI-enriched fields for a single input word")
data class WordFillGapsResultItem(
    val inputSourceWord: String,
    val sourceWord: String?,
    val translation: String?,
    val definition: String?,
    val type: WordType?,
    val extraMark: WordExtraMark?,
    val error: String?,
)
