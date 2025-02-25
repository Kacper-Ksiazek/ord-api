package com.backend.ord.api.responses.games

import com.backend.ord.config.ScoringResult
import java.util.*

data class IdentifiableProperAnswer(
    val id: UUID,
    val expectedAnswer: String,
    val userAnswer: String?,
    val result: ScoringResult,
)
