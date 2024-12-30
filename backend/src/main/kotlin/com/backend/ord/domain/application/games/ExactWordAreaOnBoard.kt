package com.backend.ord.domain.application.games

// TODO: Refactor this field name and reconsider the class's name
data class ExactWordAreaOnBoard(
    /**
     * The (X,Y) position of the beginning of the word
     */
    val start: Pair<Int, Int>,

    /**
     * The (X,Y) position of the end of the word
     */
    val end: Pair<Int, Int>,
) {
    override fun toString(): String {
        return "${start.first},${start.second},${end.first},${end.second}"
    }
}