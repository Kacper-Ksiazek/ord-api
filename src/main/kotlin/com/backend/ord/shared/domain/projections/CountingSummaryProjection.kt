package com.backend.ord.shared.domain.projections

/**
 * Projection interface for counting summaries - used for native HQL queries.
 */
interface CountingSummaryProjection {
    val today: Int?
    val week: Int?
    val month: Int?
}