package com.ord.core.word.repositories

data class WordOverviewCounts(
    val total: Long,
    val activeCount: Long,
    val unverifiedSourceCount: Long,
)
