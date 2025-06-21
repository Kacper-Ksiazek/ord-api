package com.ord.features.game.model.ongoing_game.json

import java.util.*

/**
 * Represents the proper answers for a crossword game.
 *
 * @property finalWord The final word that the player must guess. It is a string of letters from crossword questions.
 * @property questions A map of question IDs to the words that the player must guess to answer the questions.
 */
data class CrosswordProperAnswers(
    val finalWord: String,
    val questions: Map<UUID, String>
)