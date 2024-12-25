package com.backend.ord.domain.embedded.game_instructions

enum class CrosswordWordDirection {
    HORIZONTAL,
    VERTICAL
}

data class CrosswordQuestion(
    val word: String,
    val clue: String,

    val direction: CrosswordWordDirection,

    val endCoordinates: Pair<Int, Int>,
    val startCoordinates: Pair<Int, Int>,

    val answerComponent: List<AnswerComponent>? = null
)

data class AnswerComponent(
    val indexInWord: Int,
    val indexInPassword: Int,
)

data class CrosswordInstruction(
    val answer: String,
    val answerExplanation: String,
    val questions: Set<CrosswordQuestion>,
    val board: List<List<String?>> // TODO: Get rid of it here
)