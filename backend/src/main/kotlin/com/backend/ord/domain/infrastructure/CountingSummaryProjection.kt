package com.backend.ord.domain.infrastructure

interface CountingSummaryProjection {
    val today: Int?
    val week: Int?
    val month: Int?
}

