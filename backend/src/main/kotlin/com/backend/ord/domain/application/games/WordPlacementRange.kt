package com.backend.ord.domain.application.games

// TODO: Refactor this field name and reconsider the class's name
data class WordPlacementRange(
    /**
     * The (X,Y) position of the beginning of the word
     */
    val start: Coordinates,

    /**
     * The (X,Y) position of the end of the word
     */
    val end: Coordinates
) {
    override fun toString(): String {
        return "$start,$end"
    }
}