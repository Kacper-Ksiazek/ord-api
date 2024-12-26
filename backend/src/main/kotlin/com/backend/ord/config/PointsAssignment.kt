package com.backend.ord.config

import com.backend.ord.utils.data_classes.Percentage

/**
 * The points that can be assigned to an answer in a game.
 * These points are further used to compute the score of a game in percentage, therefore they cannot be negative.
 */
enum class AnswerScore(val value: Double) {
    CORRECT(1.0),
    HALF_CORRECT(0.5),
    INCORRECT(0.0);

    /**
     * Convert the answer components to the points stored in the database.
     */
    fun convertToDBPoints(): Double {
        return when (this) {
            CORRECT -> DatabasePoints.CORRECT.value
            HALF_CORRECT -> DatabasePoints.HALF_CORRECT.value
            INCORRECT -> DatabasePoints.INCORRECT.value
        }
    }
}


/**
 * The points which will be stored in the database and further reflect the progress of the user with the particular word.
 * These points can be negative and are used to determine if a word is complete or not.
 */
enum class DatabasePoints(val value: Double) {
    COMPLETE_WORD_THRESHOLD(7.0),

    CORRECT(2.0),
    INCORRECT(-1.0),
    HALF_CORRECT(1.0);

    companion object {
        /**
         * Helper function to check if a given score meets the threshold for a complete word.
         */
        fun isCompletableThresholdMet(score: Double): Boolean {
            return score >= COMPLETE_WORD_THRESHOLD.value
        }
    }
}


object ComponentsPointsRatio {
    object Crossword {
        val QUESTIONS: Percentage = Percentage(75.0)
        val FINAL_WORD: Percentage = Percentage(25.0)
    }
}