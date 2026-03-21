package com.ord.core.gpt_tokens_usage_analytics.requests.dto

import java.time.Instant
import java.time.temporal.ChronoUnit

enum class AnalyticsPeriod {
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_90_DAYS,
    ALL_TIME;

    fun toInstant(): Instant? = when (this) {
        LAST_7_DAYS -> Instant.now().minus(7, ChronoUnit.DAYS)
        LAST_30_DAYS -> Instant.now().minus(30, ChronoUnit.DAYS)
        LAST_90_DAYS -> Instant.now().minus(90, ChronoUnit.DAYS)
        ALL_TIME -> null
    }
}
