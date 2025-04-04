package com.backend.ord.domain.application.games.crossword.board

enum class CrosswordWordDirection {
    HORIZONTAL,
    VERTICAL;

    fun opposite(): CrosswordWordDirection {
        return if (this == HORIZONTAL) {
            VERTICAL
        } else {
            HORIZONTAL
        }
    }
}