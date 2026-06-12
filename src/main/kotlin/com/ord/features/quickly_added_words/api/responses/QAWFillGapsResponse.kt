package com.ord.features.quickly_added_words.api.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "AI-enriched QAW fields for each requested word, in the same order as the request")
data class QAWFillGapsResponse(
    @Schema(description = "Enrichment results, one per input item")
    val items: List<QAWFillGapsResultItem>,
)
