package com.backend.ord.utils.games

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.getNumberOfLettersToReveal
import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import com.backend.ord.services.ai.dto.crossword.CrosswordWordDirection
import kotlin.math.max

const val HIDDEN_CHARACTER: Char = '*'
private val SPECIAL_CHARS: Set<Char> = setOf(' ', '\'', '-', '’', '_')

fun isHiddenChar(char: Char): Boolean {
    return char == HIDDEN_CHARACTER
}

fun isSpecialChar(char: Char): Boolean {
    return SPECIAL_CHARS.contains(char)
}

/**
 *
 */
fun hideLettersInWord(
    wordToHide: String,
    difficulty: GameDifficulty,
    lettersToReveal: Set<Int> = emptySet()
): String {
    val numberOfLettersToReveal: Int = max(difficulty.getNumberOfLettersToReveal() - lettersToReveal.size, 0)
    val indexesOfLettersToReveal: List<Int> = wordToHide
        .indices
        .filter { !lettersToReveal.contains(it) }
        .shuffled()
        .take(numberOfLettersToReveal)
        .plus(lettersToReveal)

    return wordToHide.mapIndexed { index, currentChar ->
        if (SPECIAL_CHARS.contains(currentChar) || indexesOfLettersToReveal.contains(index)) {
            currentChar
        } else {
            HIDDEN_CHARACTER
        }
    }.joinToString("")
}


fun MutableList<MutableList<String?>>.updateWord(question: CrosswordQuestion) {
    val position = question.position ?: throw IllegalStateException("The question is not placed on the board yet.")

    question.word.forEachIndexed { index, letter ->
        val (x, y) = position.coordinates.start

        when (position.direction) {
            CrosswordWordDirection.HORIZONTAL -> this[y][x + index] = letter.toString()
            CrosswordWordDirection.VERTICAL -> this[y + index][x] = letter.toString()
        }
    }
}