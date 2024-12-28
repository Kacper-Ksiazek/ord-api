package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.bases.GameToBeFinishedBase

interface CrosswordUserAnswersQuestion {
    /**
     * The answer to the clue at the given coordinates.
     */
    val word: String

    // Both of the following fields are used to identify the word in the crossword.
    val endCoordinates: Pair<Int, Int>
    val startCoordinates: Pair<Int, Int>

    // TODO: Create Position field with new data class and handle all coordinates there
    /**
     * The coordinates of the word in the crossword.
     * The format is "startX,startY,endX,endY".
     */
    fun computeCoordinates(): String {
        return "${startCoordinates.first},${startCoordinates.second},${endCoordinates.first},${endCoordinates.second}"
    }
}


interface CrosswordUserAnswers {
    /**
     * The crossword's final answer - this is this word or phrase constructed after answering all the questions.
     */
    val answer: String

    /**
     * The user's answers to the questions.
     */
    val questionsAnswers: Set<CrosswordUserAnswersQuestion>
}

interface CrosswordToFinishRequest : GameToBeFinishedBase<CrosswordUserAnswers>