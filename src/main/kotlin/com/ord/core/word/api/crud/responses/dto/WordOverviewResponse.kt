package com.ord.core.word.api.crud.responses.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Overview of words split by lifecycle status")
data class WordOverviewResponse(
    @Schema(description = "Total number of words", example = "74")
    val total: Long,

    @Schema(description = "Number of active words", example = "56")
    val activeCount: Long,

    @Schema(description = "Number of captured words", example = "18")
    val capturedCount: Long,
)
