package com.backend.ord.domain.persistence.embedded.game_instructions

import com.backend.ord.domain.application.games.WordPlacementRange

enum class CrosswordWordDirection {
    HORIZONTAL,
    VERTICAL;

    fun opposite(): CrosswordWordDirection {
        return if (this == HORIZONTAL) {
            VERTICAL
        } else {
            HORIZONTAL
        }
    }
}

data class CrosswordQuestion(
    val word: String,
    val clue: String,

    val direction: CrosswordWordDirection,

    val coordinates: WordPlacementRange,

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
)