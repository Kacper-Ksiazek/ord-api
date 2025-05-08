package com.backend.ord.enums.persistence.game

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
 * Returns the number of words for the crossword based on the difficulty.
 *
 * It returns:
 * - 8 for [GameDifficulty.EASY]
 * - 14 for [GameDifficulty.MEDIUM]
 * - 20 for [GameDifficulty.HARD]
 */
fun GameDifficulty.getNumberOfWordsForWordsTypingGame(): Int {
    return when (this) {
        GameDifficulty.EASY -> 8
        GameDifficulty.MEDIUM -> 12
        GameDifficulty.HARD -> 16
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


fun GameDifficulty.getNumberOfLettersToReveal(): Int {
    return when (this) {
        /**
         * [EASY] Probability:
         * - 0: 30%
         * - 1: 40%
         * - 2: 20%
         * - 3: 10%
         */
        GameDifficulty.EASY -> listOf(
            *List(3) { 0 }.toTypedArray(),
            *List(4) { 1 }.toTypedArray(),
            *List(2) { 2 }.toTypedArray(),
            *List(1) { 3 }.toTypedArray(),
        ).random()

        /**
         * [MEDIUM] Probability:
         * - 0: 30%
         * - 1: 40%
         * - 2: 20%
         * - 3: 10%
         */
        GameDifficulty.MEDIUM -> listOf(
            *List(3) { 0 }.toTypedArray(),
            *List(4) { 1 }.toTypedArray(),
            *List(2) { 2 }.toTypedArray(),
            *List(1) { 3 }.toTypedArray(),
        ).random()

        /**
         * [HARD] Probability:
         * - 0: 50%
         * - 1: 30%
         * - 2: 10%
         * - 3: 10%
         */
        GameDifficulty.HARD -> listOf(
            *List(5) { 0 }.toTypedArray(),
            *List(3) { 1 }.toTypedArray(),
            *List(1) { 2 }.toTypedArray(),
            *List(1) { 3 }.toTypedArray(),
        ).random()
    }
}