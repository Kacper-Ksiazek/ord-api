package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.bases.GameToBeFinishedBase
import java.util.*

interface CrosswordUserAnswersQuestion {
    /**
     * The crossword question's ID.
     */
    val id: UUID

    /**
     * The answer to the clue at the given coordinates.
     */
    val word: String
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