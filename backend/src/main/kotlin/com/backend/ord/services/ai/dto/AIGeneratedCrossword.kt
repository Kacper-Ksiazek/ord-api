package com.backend.ord.services.ai.dto

import java.util.*

data class AIGeneratedCrosswordQuestion(
    val id: UUID = UUID.randomUUID(),

    val word: String,
    val clue: String,
)

data class AIGeneratedCrossword(
    val answer: String,
    val answerExplanation: String,
    val questions: List<AIGeneratedCrosswordQuestion>
)