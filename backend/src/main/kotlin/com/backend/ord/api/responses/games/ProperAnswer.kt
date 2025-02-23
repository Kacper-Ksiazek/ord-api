package com.backend.ord.api.responses.games

import com.backend.ord.config.ScoringResult

data class ProperAnswer(
    val expectedAnswer: String,
    val userAnswer: String?,
    val result: ScoringResult,
)
