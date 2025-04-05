package com.backend.ord.api.responses.games.utils

import com.backend.ord.enums.application.game.AnswerScore
import java.util.*

data class IdentifiableProperAnswer(
    val id: UUID,
    val expectedAnswer: String,
    val userAnswer: String?,
    val score: AnswerScore,
)
