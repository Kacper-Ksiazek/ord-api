package com.ord.core.word.api.capture.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.api.annotations.validators.SafeString
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "Request to quickly capture a word for later activation")
data class CaptureWordRequest(
    @field:SafeString(fieldName = "Source word", min = 1, max = 255)
    @Schema(description = "The word or phrase to capture", example = "comprehensive", required = true)
    val sourceWord: String,

    @Schema(description = "Language of the word", example = "ENGLISH", required = true)
    val language: LanguageName,

    @field:Size(max = 255, message = "Translation must not exceed 255 characters")
    val translation: String? = null,

    @field:Size(max = 2000, message = "Definition must not exceed 2000 characters")
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)
