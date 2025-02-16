package com.backend.ord.utils.games

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.getNumberOfLettersToReveal

object CrosswordUtils {
    const val HIDDEN_CHARACTER: Char = '*'
    val SPECIAL_CHARS: Set<Char> = setOf(' ', '\'', '-', '’', '_')

    /**
     *
     */
    fun hideLettersInProperAnswer(
        wordToHide: String,
        difficulty: GameDifficulty
    ): String {
        val numberOfLettersToReveal: Int = difficulty.getNumberOfLettersToReveal()
        val indexesOfLettersToReveal = wordToHide.indices.shuffled().take(numberOfLettersToReveal)

        return wordToHide.mapIndexed { index, currentChar ->
            if (SPECIAL_CHARS.contains(currentChar) || indexesOfLettersToReveal.contains(index)) {
                currentChar
            } else {
                HIDDEN_CHARACTER
            }
        }.joinToString("")
    }


    // TODO: Implement and use this one function
    /**
     * Iterates over all words placed on the board and hides some of the
     * letters in them, according to the game difficulty.
     */
//    private fun hideLettersAcrossCrossword(
//        instruction: CrosswordInstruction,
//        board: List<List<String?>>,
//    ): String {
//        val numberOfLettersToReveal: Int = difficulty.getNumberOfLettersToReveal()
//        val indexesOfLettersToReveal = wordToHide.indices.shuffled().take(numberOfLettersToReveal)
//
//        return wordToHide.mapIndexed { index, currentChar ->
//            if (SPECIAL_CHARS.contains(currentChar) || indexesOfLettersToReveal.contains(index)) {
//                currentChar
//            } else {
//                HIDDEN_CHARACTER
//            }
//        }.joinToString("")
//    }
}