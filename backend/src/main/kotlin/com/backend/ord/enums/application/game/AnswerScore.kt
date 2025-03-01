package com.backend.ord.enums.application.game

import com.backend.ord.config.GamesConfig

/**
 * The points that can be assigned to an answer in a game.
 * These points are further used to compute the score of a game in percentage, therefore they cannot be negative.
 */
enum class AnswerScore(
    val wage: Double,
    val dbPoints: Int
) {
    CORRECT(wage = 1.0, dbPoints = GamesConfig.Points.DatabaseValues.CORRECT_ANSWER),
    HALF_CORRECT(wage = 0.5, dbPoints = GamesConfig.Points.DatabaseValues.HALF_CORRECT_ANSWER),
    INCORRECT(wage = 0.0, dbPoints = GamesConfig.Points.DatabaseValues.INCORRECT_ANSWER)
}