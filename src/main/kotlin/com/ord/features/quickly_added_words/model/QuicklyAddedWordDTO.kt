package com.ord.features.quickly_added_words.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

@Schema(description = "Quickly added word with all details")
data class QuicklyAddedWordDTO(
    @Schema(
        description = "Unique word identifier",
        example = "650e8400-e29b-41d4-a716-446655440000"
    )
    val id: UUID,

    @Schema(
        description = "The word or phrase",
        example = "comprehensive"
    )
    var word: String,

    @Schema(
        description = "Language of the word",
        example = "ENGLISH"
    )
    var language: LanguageName,

    @Schema(
        description = "Translation of the word",
        example = "comprensivo",
        nullable = true
    )
    val translation: String?,

    @Schema(
        description = "Definition or notes about the word",
        example = "Complete; including all elements",
        nullable = true
    )
    val definition: String?,

    @Schema(
        description = "Extra mark or tag for the word",
        example = "SLANG",
        nullable = true
    )
    val extraMark: WordExtraMark?,

    @Schema(
        description = "Word type classification",
        example = "ADJECTIVE",
        nullable = true
    )
    val type: WordType?,

    @Schema(
        description = "Whether the word has been approved for full word status",
        example = "false"
    )
    val isApproved: Boolean,

    @Schema(
        description = "ID of the user who added this word",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    val userId: UUID,

    @Schema(
        description = "Timestamp when the word was added",
        example = "2024-01-15T10:30:00Z"
    )
    val createdAt: Instant = Instant.now(),
)
