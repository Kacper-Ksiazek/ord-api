package com.backend.ord.api.responses.games

import com.backend.ord.enums.application.game.ScoringResult

data class ProperAnswer(
    val expectedAnswer: String,
    val userAnswer: String?,
    val result: ScoringResult,
)