package com.backend.ord.config

/**
 * The points that can be assigned to an answer in a game.
 * These points are further used to compute the score of a game in percentage.
 */
object AnswerScore {
    const val CORRECT: Double = 1.0
    const val HALF_CORRECT: Double = 0.5
    const val INCORRECT: Double = 0.0
}

object ComponentsPointsRatio {
    object Crossword {
        const val QUESTIONS: Double = 0.6
        const val TIME: Double = 0.2
        const val FINAL_WORD: Double = 0.2
    }
}