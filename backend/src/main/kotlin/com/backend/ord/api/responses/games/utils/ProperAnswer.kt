package com.backend.ord.api.responses.games.utils

import com.backend.ord.enums.application.game.AnswerScore

data class ProperAnswer(
    val expectedAnswer: String,
    val userAnswer: String?,
    val score: AnswerScore,
)