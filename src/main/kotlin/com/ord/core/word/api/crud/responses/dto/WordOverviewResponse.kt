package com.ord.core.word.api.crud.responses.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Overview of words split by learning progress and source")
data class WordOverviewResponse(
    @Schema(description = "Total number of words", example = "74")
    val total: Long,

    @Schema(description = "Number of words with learning progress", example = "56")
    val activeCount: Long,

    @Schema(description = "Number of words from unverified sources", example = "18")
    val unverifiedSourceCount: Long,
)
