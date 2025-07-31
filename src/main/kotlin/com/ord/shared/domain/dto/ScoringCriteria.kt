package com.ord.shared.domain.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ScoringCriteria(
    @Min(0) @Max(10) val score: Int,
    val comment: String? = null
)