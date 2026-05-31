package com.ord.features.quickly_added_words.api.responses

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "AI-enriched QAW fields for a single input word")
data class QAWFillGapsResultItem(
    @Schema(description = "Original word from the request", example = "verbose")
    val inputWord: String,

    @Schema(
        description = "Corrected or confirmed word spelling (null when error is set)",
        example = "verbose",
        nullable = true,
    )
    val word: String?,

    @Schema(
        description = "Translation into the user's translate-to language (null when error is set)",
        example = "rozwlekły",
        nullable = true,
    )
    val translation: String?,

    @Schema(
        description = "Brief definition in the user's generative content language (null when error is set)",
        example = "Using more words than needed; long-winded",
        nullable = true,
        maxLength = 2000,
    )
    val definition: String?,

    @Schema(
        description = "Word type classification (null when error is set)",
        example = "ADJECTIVE",
        nullable = true,
    )
    val type: WordType?,

    @Schema(
        description = "Optional register or domain mark (null when not applicable or on error)",
        example = "FORMAL",
        nullable = true,
    )
    val extraMark: WordExtraMark?,

    @Schema(
        description = "Error code when the word could not be enriched (e.g. NON_EXISTENT_WORD)",
        example = "NON_EXISTENT_WORD",
        nullable = true,
    )
    val error: String?,
)
