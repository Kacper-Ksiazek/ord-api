package com.ord.core.word.api.crud.responses.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Overview of vocabulary words")
data class WordOverviewResponse(
    @Schema(description = "Total number of words in the learning list", example = "74")
    val total: Long,

    @Schema(description = "Number of bookmarked words", example = "12")
    val bookmarkedCount: Long,
)
