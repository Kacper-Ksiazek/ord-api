package com.backend.ord.services.ai.dto.crossword

/**
 * Represents a pair of indices of a letter in a word and a letter in the password.
 *
 * @property indexInWord The index of the letter in the word.
 * @property indexInPassword The index of the letter in the password.
 */
data class LetterInCrosswordAnswer(
    val indexInWord: Int,
    val indexInPassword: Int
)
