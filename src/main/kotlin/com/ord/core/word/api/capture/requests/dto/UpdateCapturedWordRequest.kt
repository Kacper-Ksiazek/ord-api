package com.ord.core.word.api.capture.requests.dto

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.util.*

@Schema(description = "Partial update for a captured word")
data class UpdateCapturedWordRequest(
    @field:Size(min = 1, max = 255)
    val sourceWord: String? = null,

    @field:Size(max = 255)
    val translation: String? = null,

    @field:Size(max = 2000)
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)

@Schema(description = "Request to activate multiple captured words")
data class ActivateManyWordsRequest(
    val ids: List<UUID>,
)
