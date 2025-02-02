package com.backend.ord.domain.application.games

import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordWordDirection

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

    fun shift2D(verticalOffset: Int, horizontalOffset: Int) {
        println("Shifting $this by $verticalOffset vertically and $horizontalOffset horizontally")

        if (verticalOffset != 0) {
            start.shift(offset = verticalOffset, direction = CrosswordWordDirection.VERTICAL)
            end.shift(offset = verticalOffset, direction = CrosswordWordDirection.VERTICAL)
        }

        if (horizontalOffset != 0) {
            start.shift(offset = horizontalOffset, direction = CrosswordWordDirection.HORIZONTAL)
            end.shift(offset = horizontalOffset, direction = CrosswordWordDirection.HORIZONTAL)
        }
    }
}