package com.backend.ord.enums.game

enum class GameDifficulty {
    EASY,
    MEDIUM,
    HARD
}

/**
 * Returns the number of words for the crossword based on the difficulty.
 *
 * It returns:
 * - 6 for [GameDifficulty.EASY]
 * - 9 for [GameDifficulty.MEDIUM]
 * - 12 for [GameDifficulty.HARD]
 */
fun GameDifficulty.getNumberOfWordsForCrossword(): Int {
    return when (this) {
        GameDifficulty.EASY -> 6
        GameDifficulty.MEDIUM -> 9
        GameDifficulty.HARD -> 12
    }
}