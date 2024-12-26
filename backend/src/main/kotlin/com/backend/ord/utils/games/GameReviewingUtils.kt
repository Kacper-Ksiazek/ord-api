package com.backend.ord.utils.games

import com.backend.ord.config.AnswerScore
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.getNumberOfAllowedMistakes
import com.backend.ord.utils.data_classes.Percentage

object GameReviewingUtils {
    /**
     * Returns the points for the user answer based on the correctness of the answer and the difficulty of the game.
     * The points are returned as a pair of the correct word and received points.
     */
    fun getPointsForUserAnswer(
        userAnswer: String?,
        correctAnswer: String,
        difficulty: GameDifficulty
    ): Pair<String, AnswerScore> {
        if (userAnswer == null) {
            return Pair(correctAnswer, AnswerScore.INCORRECT)
        }

        // Compare two words letter by letter and count the number of non-matching letters
        val incorrectLettersCount = (correctAnswer.lowercase() zip userAnswer.lowercase())
            .count { (correctChar, userChar) -> correctChar != userChar }

        return Pair(
            correctAnswer,
            when {
                incorrectLettersCount == 0 -> AnswerScore.CORRECT
                incorrectLettersCount <= difficulty.getNumberOfAllowedMistakes() -> AnswerScore.HALF_CORRECT
                else -> AnswerScore.INCORRECT
            }
        )
    }

    fun computeFinalScoreComponent(
        maxPoints: Int,
        receivedPoints: Int,
        pointsInTotal: Int,
        componentPointsRation: Percentage
    ): Int {


    }
}