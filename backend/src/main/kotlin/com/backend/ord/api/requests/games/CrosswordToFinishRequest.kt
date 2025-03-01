package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.bases.GameToBeFinishedBase
import com.backend.ord.api.requests.games.data.WordUserAnswer


interface CrosswordUserAnswers {
    /**
     * The crossword's final answer - this is this word or phrase constructed after answering all the questions.
     */
    val answer: String

    /**
     * The user's answers to the questions.
     */
    val questionsAnswers: Set<WordUserAnswer>
}

interface CrosswordToFinishRequest : GameToBeFinishedBase<CrosswordUserAnswers>