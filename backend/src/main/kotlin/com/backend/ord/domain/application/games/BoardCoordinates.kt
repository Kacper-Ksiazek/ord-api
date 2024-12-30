package com.backend.ord.domain.application.games

data class BoardCoordinates(
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