package com.backend.ord.api.responses.gpt_tokens_usage

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