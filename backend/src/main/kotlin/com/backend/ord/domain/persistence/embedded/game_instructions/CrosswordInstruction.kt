package com.backend.ord.domain.persistence.embedded.game_instructions

import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.domain.application.games.WordPlacementRange
import java.util.*

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
    val id: UUID,

    var word: String, // Prepare a private setter for this property
    val clue: String,

    val direction: CrosswordWordDirection,

    val coordinates: WordPlacementRange,

    var answerComponents: MutableList<AnswerComponent>? = null
) {
    /**
     * Adds a new answer component to the list of answer components.
     */
    fun addAnswerComponent(newAnswerComponent: AnswerComponent) {
        if (answerComponents == null) answerComponents = mutableListOf<AnswerComponent>()

        answerComponents?.add(newAnswerComponent)
    }

    /**
     * Returns the coordinates of the letter at the given index in the word.
     */
    fun getCoordinatesOfLetterAtIndex(index: Int): Coordinates {
        val (x, y) = this.coordinates.start

        return when (this.direction) {
            CrosswordWordDirection.HORIZONTAL -> Coordinates(x + index, y)
            CrosswordWordDirection.VERTICAL -> Coordinates(x, y + index)
        }
    }
}

data class AnswerComponent(
    val indexInWord: Int,
    val indexInPassword: Int,
)

data class CrosswordInstruction(
    val answerExplanation: String,
    val answer: String,
    val questions: Set<CrosswordQuestion>,
)