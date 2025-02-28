package com.backend.ord.enums.application.game

/**
 * The points that can be assigned to an answer in a game.
 * These points are further used to compute the score of a game in percentage, therefore they cannot be negative.
 */
enum class AnswerScore(
    val value: Double,
    val resultName: ScoringResult
) {
    CORRECT(1.0, ScoringResult.CORRECT),
    HALF_CORRECT(0.5, ScoringResult.HALF_CORRECT),
    INCORRECT(0.0, ScoringResult.INCORRECT);

    /**
     * Convert the answer components to the points stored in the database.
     */
    fun convertToDBPoints(): DatabasePoint {
        return when (this) {
            CORRECT -> DatabasePoint.CORRECT
            HALF_CORRECT -> DatabasePoint.HALF_CORRECT
            INCORRECT -> DatabasePoint.INCORRECT
        }
    }
}