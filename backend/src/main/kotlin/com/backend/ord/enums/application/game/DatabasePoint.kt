package com.backend.ord.enums.application.game

import com.backend.ord.config.GamesConfig

/**
 * The points which will be stored in the database and further reflect the progress of the user with the particular word.
 * These points can be negative and are used to determine if a word is complete or not.
 */
enum class DatabasePoint(val value: Int) {
    COMPLETE_WORD_THRESHOLD(GamesConfig.Points.COMPLETE_WORD_THRESHOLD),

    CORRECT(GamesConfig.Points.Wages.CORRECT_ANSWER),
    INCORRECT(GamesConfig.Points.Wages.INCORRECT_ANSWER),
    HALF_CORRECT(GamesConfig.Points.Wages.HALF_CORRECT_ANSWER);

    companion object {
        /**
         * Helper function to check if a given score meets the threshold for a complete word.
         */
        fun isCompletableThresholdMet(score: Double): Boolean {
            return score >= COMPLETE_WORD_THRESHOLD.value
        }
    }
}