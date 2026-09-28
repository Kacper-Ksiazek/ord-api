package com.ord.core.word.api.crud.responses.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Words from the lookup that the user already has defined")
data class LookupDefinedWordsResponse(
    val words: List<DefinedWordResponse>,
)

@Schema(description = "A vocabulary word the user already has defined")
data class DefinedWordResponse(
    val id: UUID,
    val sourceWord: String,
)
