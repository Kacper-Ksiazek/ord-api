package com.backend.ord.utils.games

import com.backend.ord.config.AnswerScore
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.getNumberOfAllowedMistakes

object GameReviewingUtils {
    /**
     * Returns the points for the user answer based on the correctness of the answer and the difficulty of the game.
     * The points are returned as a pair of the correct word and received points.
     */
    fun getPointsForUserAnswer(
        userAnswer: String?,
        correctAnswer: String,
        difficulty: GameDifficulty
    ): Pair<String, Double> {
        if (userAnswer == null) {
            return Pair(correctAnswer, AnswerScore.INCORRECT)
        }

        val incorrectLetters = (correctAnswer.lowercase() zip userAnswer.lowercase())
            .count { (correctChar, userChar) -> correctChar != userChar }

        return Pair(
            correctAnswer,
            when {
                incorrectLetters == 0 -> AnswerScore.CORRECT
                incorrectLetters <= difficulty.getNumberOfAllowedMistakes() -> AnswerScore.HALF_CORRECT
                else -> AnswerScore.INCORRECT
            }
        )

    }
}