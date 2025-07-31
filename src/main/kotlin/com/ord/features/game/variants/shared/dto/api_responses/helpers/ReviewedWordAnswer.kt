package com.ord.features.game.variants.shared.dto.api_responses.helpers

import com.ord.features.game.variants.shared.enums.WordAnswerScore

data class ReviewedWordAnswer(
    val expectedAnswer: String,
    val userAnswer: String?,
    val score: WordAnswerScore,
)