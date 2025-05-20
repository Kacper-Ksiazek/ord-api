package com.backend.ord.utils.games

import com.backend.ord.features.ongoing_game.variants.crossword.dto.helpers.board.CrosswordWordDirection
import com.backend.ord.features.ongoing_game.variants.crossword.dto.helpers.question.CrosswordQuestion

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