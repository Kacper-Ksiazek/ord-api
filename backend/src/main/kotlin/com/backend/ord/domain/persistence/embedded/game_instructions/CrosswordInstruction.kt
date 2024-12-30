package com.backend.ord.domain.persistence.embedded.game_instructions

import com.backend.ord.domain.application.games.BoardCoordinates

enum class CrosswordWordDirection {
    HORIZONTAL,
    VERTICAL
}

data class CrosswordQuestion(
    val word: String,
    val clue: String,

    val direction: CrosswordWordDirection,

    val coordinates: BoardCoordinates,

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