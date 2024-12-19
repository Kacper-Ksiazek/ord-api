package com.backend.ord.services.ai.dto

data class AIGeneratedCrosswordQuestion(
    val word: String,
    val clue: String,
)

data class AIGeneratedCrossword(
    val answer: String,
    val answerExplanation: String,
    val questions: List<AIGeneratedCrosswordQuestion>
)