package com.backend.ord.features.game.variants.crossword.dto.helpers.board

data class Coordinates(
    var x: Int,
    var y: Int
) {
    init {
        require(x >= 0) { "x must be greater than or equal to 0" }
        require(y >= 0) { "y must be greater than or equal to 0" }
    }

    /**
     * Shifts the coordinates by the given offset in the given direction.
     */
    fun shift(offset: Int, direction: CrosswordWordDirection): Coordinates {
        if (direction == CrosswordWordDirection.HORIZONTAL) {
            this.x += offset
        } else {
            this.y += offset
        }

        return this
    }

    /**
     * Duplicate the coordinates and shift the new coordinates by the given offset in the given direction.
     */
    fun copyAndShift(offset: Int, direction: CrosswordWordDirection): Coordinates {
        return copy().shift(offset, direction)
    }

    override fun toString(): String {
        return "$x,$y"
    }
}