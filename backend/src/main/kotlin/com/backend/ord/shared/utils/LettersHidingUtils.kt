package com.backend.ord.shared.utils

import kotlin.math.max

const val HIDDEN_CHARACTER: Char = '*'
val SPECIAL_CHARS: Set<Char> = setOf(' ', '\'', '-', '’', '_')

fun isHiddenChar(char: Char): Boolean {
    return char == HIDDEN_CHARACTER
}

fun Char.isHidden(): Boolean {
    return isHiddenChar(this)
}

fun isSpecialChar(char: Char): Boolean {
    return SPECIAL_CHARS.contains(char)
}

fun Char.isSpecial(): Boolean {
    return isSpecialChar(this)
}

fun hideLettersInWord(
    wordToHide: String,
    numberOfLettersToReveal: Int,
    lettersToReveal: Set<Int> = emptySet()
): String {
    val numberOfLettersToReveal: Int = max(numberOfLettersToReveal - lettersToReveal.size, 0)
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

fun String.hideLetters(
    numberOfLettersToReveal: Int = 0,
    lettersToReveal: Set<Int> = emptySet()
): String {
    return hideLettersInWord(
        wordToHide = this,
        numberOfLettersToReveal = numberOfLettersToReveal,
        lettersToReveal = lettersToReveal
    )
}