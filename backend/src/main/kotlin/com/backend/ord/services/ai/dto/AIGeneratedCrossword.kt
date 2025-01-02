package com.backend.ord.services.ai.dto

import java.util.*

data class AIGeneratedCrosswordQuestion(
    val id: UUID = UUID.randomUUID(),

    var word: String,
    val clue: String,
)

data class AIGeneratedCrossword(
    var answer: String,
    val answerExplanation: String,
    val questions: List<AIGeneratedCrosswordQuestion>
)