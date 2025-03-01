package com.backend.ord.utils.games

import com.backend.ord.enums.application.game.AnswerScore
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.getNumberOfAllowedMistakes
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
    ): AnswerScore {
        if (userAnswer == null) {
            return AnswerScore.INCORRECT
        }

        // Compare two words letter by letter and count the number of non-matching letters
        val incorrectLettersCount = (correctAnswer.lowercase() zip userAnswer.lowercase())
            .count { (correctChar, userChar) -> correctChar != userChar }

        return when {
            incorrectLettersCount == 0 -> AnswerScore.CORRECT
            incorrectLettersCount <= difficulty.getNumberOfAllowedMistakes() -> AnswerScore.HALF_CORRECT
            else -> AnswerScore.INCORRECT
        }
    }

    /**
     * Compute the part of the final score for one aspect of the game
     */
    fun computeFinalScoreComponent(
        receivedPoints: Double,
        maxPoints: Double,
        moduleRatio: Percentage = Percentage(100),
        totalPointsForAllModules: Int = 100
    ): Int {
        val points: Double = (receivedPoints / maxPoints) * totalPointsForAllModules

        return (moduleRatio * points).toInt()
    }
}