package com.ord.core.word.api.crud.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Look up which of the given words the authenticated user already has defined")
data class LookupDefinedWordsRequest(
    @field:NotNull(message = "Language is required")
    @field:ValidLanguageName
    val language: LanguageName,

    @field:NotNull(message = "Source words are required")
    @field:Size(max = 50, message = "Source words list must contain at most 50 items")
    val sourceWords: List<String>,
)
