package com.backend.ord.features.ongoing_game.variants.crossword.dto.helpers.question

import com.backend.ord.features.ongoing_game.variants.crossword.dto.helpers.board.CrosswordWordDirection
import com.backend.ord.features.ongoing_game.variants.crossword.dto.helpers.board.WordPlacementRange

/**
 * Represents the position of a question on the crossword board.
 *
 * @property coordinates The range of coordinates on the board where the question is placed, containing the start and end coordinates.
 * @property direction The direction of the question on the board, either horizontal or vertical.
 */
data class QuestionBoardPosition(
    val coordinates: WordPlacementRange,
    val direction: CrosswordWordDirection
)