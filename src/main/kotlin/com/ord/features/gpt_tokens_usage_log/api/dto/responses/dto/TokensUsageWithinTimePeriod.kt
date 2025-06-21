package com.ord.features.gpt_tokens_usage_log.api.dto.responses.dto

data class TokensUsageWithinTimePeriod<DataType>(
    /**
     * Month in which the statistics were calculated. Numbers from 1 to 12.
     */
    val month: Int,

    /**
     * Year in which the statistics were calculated.
     */
    val year: Int,

    /**
     * List of statistics for the given time period.
     */
    val data: List<DataType>
)