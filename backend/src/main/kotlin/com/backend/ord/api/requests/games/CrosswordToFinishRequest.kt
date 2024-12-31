package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.bases.GameToBeFinishedBase
import com.backend.ord.domain.application.games.WordPlacementRange

interface CrosswordUserAnswersQuestion {
    /**
     * The answer to the clue at the given coordinates.
     */
    val word: String

    /**
     * Coordinates of the word, used to identify the word in the crossword by its position.
     */
    val coordinates: WordPlacementRange
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