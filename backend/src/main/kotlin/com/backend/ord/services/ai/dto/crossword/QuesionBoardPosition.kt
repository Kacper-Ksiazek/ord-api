package com.backend.ord.services.ai.dto.crossword

import com.backend.ord.domain.application.games.WordPlacementRange

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
