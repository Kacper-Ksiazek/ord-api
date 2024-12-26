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

/**
 * Returns the number of allowed mistakes for the crossword based on the difficulty.
 *
 * It returns:
 * - 2 for [GameDifficulty.EASY]
 * - 1 for [GameDifficulty.MEDIUM]
 * - 0 for [GameDifficulty.HARD]
 */
fun GameDifficulty.getNumberOfAllowedMistakes(): Int {
    return when (this) {
        GameDifficulty.EASY -> 2
        GameDifficulty.MEDIUM -> 1
        GameDifficulty.HARD -> 0
    }
}

// TODO: Add function which returns the maximum time for a game based on the difficulty and the game type
// TODO: Additionally, extends the generated game response base to contain the maximum time for the game
