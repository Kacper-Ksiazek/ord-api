package com.ord.core.word.api.ai.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

@Schema(description = "Request to AI-fill missing fields for captured words")
data class WordFillGapsRequest(
    val language: LanguageName,

    @field:Valid
    @field:Size(min = 1, max = 20, message = "Items list must contain between 1 and 20 words")
    val items: List<WordFillGapsItem>,
)

@Schema(description = "A single word to enrich with AI-generated metadata")
data class WordFillGapsItem(
    @field:com.ord.shared.api.annotations.validators.SafeString(fieldName = "Source word", min = 1, max = 255)
    val sourceWord: String,
)
