package com.backend.ord.utils.games

import com.backend.ord.domain.application.games.crossword.CrosswordQuestion
import com.backend.ord.domain.application.games.crossword.board.CrosswordWordDirection

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