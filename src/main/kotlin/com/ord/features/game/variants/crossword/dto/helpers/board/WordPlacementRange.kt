package com.ord.features.game.variants.crossword.dto.helpers.board

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