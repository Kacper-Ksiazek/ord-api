package com.ord.features.quickly_added_words.api.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Quickly added words overview with approval status counts")
data class QAWOverviewResponse(
    @Schema(description = "Total number of quickly added words for the user", example = "42")
    val total: Long,

    @Schema(description = "Number of approved quickly added words", example = "35")
    val approvedCount: Long,

    @Schema(description = "Number of unapproved quickly added words", example = "7")
    val unapprovedCount: Long,
)
