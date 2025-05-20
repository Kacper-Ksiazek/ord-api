package com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers

import com.backend.ord.features.ongoing_game.variants.shared.enums.AnswerScore

data class ProperAnswer(
    val expectedAnswer: String,
    val userAnswer: String?,
    val score: AnswerScore,
)