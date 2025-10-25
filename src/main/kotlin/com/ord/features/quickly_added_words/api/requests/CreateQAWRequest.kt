package com.ord.features.quickly_added_words.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.api.annotations.validators.SafeString
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "Request to create a new quickly added word")
data class CreateQAWRequest(
    @field:SafeString(fieldName = "Word", min = 1, max = 255)
    @Schema(
        description = "The word or phrase to add",
        example = "comprehensive",
        required = true,
        minLength = 1,
        maxLength = 255
    )
    val word: String,

    @Schema(
        description = "Language of the word",
        example = "ENGLISH",
        required = true
    )
    val language: LanguageName,

    @field:Size(max = 2000, message = "Definition must not exceed 2000 characters")
    @Schema(
        description = "Optional definition or notes about the word",
        example = "Complete; including all or nearly all elements or aspects",
        nullable = true,
        maxLength = 2000
    )
    val definition: String? = null,

    @Schema(
        description = "Optional extra mark or tag for the word",
        example = "SLANG",
        nullable = true
    )
    val extraMark: WordExtraMark? = null,

    @Schema(
        description = "Optional word type classification",
        example = "ADJECTIVE",
        nullable = true
    )
    val type: WordType? = null,
)
