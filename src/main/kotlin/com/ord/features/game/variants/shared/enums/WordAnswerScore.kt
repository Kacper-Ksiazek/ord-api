package com.ord.features.game.variants.shared.enums

import com.ord.config.GamesConfig
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfAllowedMistakes
import com.ord.shared.annotations.ExportToOpenAPI
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

/**
 * The points that can be assigned to an answer in a game.
 * These points are further used to compute the score of a game in percentage, therefore they cannot be negative.
 */
@ExportToOpenAPI
enum class WordAnswerScore(
    val wage: Double,
    val dbPoints: Int
) {
    CORRECT(wage = 1.0, dbPoints = GamesConfig.WordPoints.DatabaseValues.CORRECT_ANSWER),
    HALF_CORRECT(wage = 0.5, dbPoints = GamesConfig.WordPoints.DatabaseValues.HALF_CORRECT_ANSWER),
    INCORRECT(wage = 0.0, dbPoints = GamesConfig.WordPoints.DatabaseValues.INCORRECT_ANSWER);

    companion object {
        /**
         * Returns the points for the user answer based on the correctness of the answer and the difficulty of the game.
         * The points are returned as a pair of the correct word and received points.
         */
        fun reviewUserAnswer(
            userAnswer: String?,
            expectedAnswer: String,
            difficulty: GameDifficulty
        ): WordAnswerScore {
            if (userAnswer == null) {
                return INCORRECT
            }

            // Compare two words letter by letter and count the number of non-matching letters
            val incorrectLettersCount = (expectedAnswer.lowercase() zip userAnswer.lowercase())
                .count { (correctChar, userChar) -> correctChar != userChar }

            return when {
                incorrectLettersCount == 0 -> CORRECT
                incorrectLettersCount <= difficulty.getNumberOfAllowedMistakes() -> HALF_CORRECT
                else -> INCORRECT
            }
        }

        fun fromDouble(@Min(0) @Max(1) accuracy: Double): WordAnswerScore {
            return when {
                accuracy >= 0.9 -> CORRECT
                accuracy >= 0.5 -> HALF_CORRECT
                else -> INCORRECT
            }
        }
    }
}