package com.ord.core.gpt_tokens_usage_analytics.responses.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Single GPT tokens usage record")
data class GptTokensUsageRecordDTO(
    @Schema(description = "Record ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: UUID,

    @Schema(description = "Type of operation that consumed tokens", example = "CONVERSATION_AI_RESPONSE")
    val operationType: String,

    @Schema(description = "GPT model used", example = "gpt-4o-mini")
    val model: String,

    @Schema(description = "Number of input tokens consumed", example = "150")
    val inputTokens: Int,

    @Schema(description = "Number of output tokens generated", example = "200")
    val outputTokens: Int,

    @Schema(description = "Timestamp when the record was created")
    val createdAt: Instant
)
