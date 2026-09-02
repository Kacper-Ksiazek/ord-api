package com.ord.core.word.api.capture.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.api.annotations.validators.SafeString
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

@Schema(description = "Public bulk capture request identified by user email")
data class PublicWordsBulkCaptureRequest(
    @field:NotBlank
    @field:Email
    val userEmail: String,

    @field:NotEmpty
    @field:Valid
    @field:Size(min = 1, max = 100, message = "Words list must contain between 1 and 100 items")
    val words: List<PublicCaptureWordItem>,
)

@Schema(description = "Single word item for public bulk capture")
data class PublicCaptureWordItem(
    @field:SafeString(fieldName = "Source word", min = 1, max = 255)
    val sourceWord: String,

    val language: LanguageName,

    @field:Size(max = 255)
    val translation: String? = null,

    @field:Size(max = 2000)
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)
