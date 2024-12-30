package com.backend.ord.domain.application.games

import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

class Board {
    val cells: MutableList<MutableList<String?>>

    constructor(dimensions: Pair<Int, Int>) {
        val (rows, cols) = dimensions

        cells = MutableList(rows) { MutableList(cols) { null } }
    }

    companion object {
        object Symbols {
            const val SEPARATOR = "__SEPARATOR"
        }
    }

    fun insertSeparator(coordinates: Pair<Int, Int>) {
        val (x, y) = coordinates

        this.cells[x][y] = Symbols.SEPARATOR
    }

    /**
     * Check whether a word fits on the board at the given coordinates and direction.
     */
    fun checkIfWordFits(
        wordToFit: String,
        startCoordinates: Pair<Int, Int>,
        direction: CrosswordWordDirection
    ): Boolean {
        val wordSize: Int = wordToFit.length

        val x = startCoordinates.first
        val y = startCoordinates.second

        if (x < 0 || y < 0) {
            return false
        }

        if (direction == CrosswordWordDirection.HORIZONTAL) {
            // Check if the word fits horizontally - meaning if it fits within the board's width
            if (x + wordSize > this.cells[0].size) {
                return false
            }

            // Check all the cells in the row where the word is supposed to be placed without the one cell in which the word starts
            for (i in 0 until wordSize) {
                val boardValueAtGivenLocation: String? = this.cells[y][x + i]
                val questionValueAtGivenIndex: String = wordToFit[i].toString()

                if (this.cells[y][x + i] != null && boardValueAtGivenLocation != questionValueAtGivenIndex) {
                    return false
                }
            }
        } else {
            // Check if the word fits vertically - meaning if it fits within the board's height
            if (y + wordSize > this.cells.size) {
                return false
            }

            for (i in 0 until wordSize) {
                val boardValueAtGivenLocation: String? = this.cells[y + i][x]
                val questionValueAtGivenIndex: String = wordToFit[i].toString()

                if (this.cells[y + i][x] != null && boardValueAtGivenLocation != questionValueAtGivenIndex) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Place a word on the board at the given coordinates and direction and add it to the list of questions.
     */
    fun place(
        startCoordinates: Pair<Int, Int>,
        direction: CrosswordWordDirection,
        aiGeneratedQuestion: AIGeneratedCrosswordQuestion,
        questionsToInstruction: MutableSet<CrosswordQuestion>
    ): CrosswordQuestion {
        val wordSize: Int = aiGeneratedQuestion.word.length
        val endCoordinates: Pair<Int, Int>

        if (direction == CrosswordWordDirection.HORIZONTAL) {
            val x = startCoordinates.first
            val y = startCoordinates.second

            for (i in 0 until wordSize) {
                this.cells[y][x + i] = aiGeneratedQuestion.word[i].toString()
            }

            endCoordinates = Pair(x + wordSize - 1, y)
        } else {
            val x = startCoordinates.first
            val y = startCoordinates.second

            for (i in 0 until wordSize) {
                this.cells[y + i][x] = aiGeneratedQuestion.word[i].toString()
            }

            endCoordinates = Pair(x, y + wordSize - 1)
        }

        val result = CrosswordQuestion(
            word = aiGeneratedQuestion.word,
            clue = aiGeneratedQuestion.clue,
            direction = direction,
            coordinates = BoardCoordinates(
                start = startCoordinates,
                end = endCoordinates
            ),
        )

        questionsToInstruction.add(result)

        return result
    }

    /**
     * Place a word on the board at the given coordinates and direction if it fits, otherwise return null.
     */
    fun placeIfFits(
        aiGeneratedQuestion: AIGeneratedCrosswordQuestion,
        startCoordinates: Pair<Int, Int>,
        direction: CrosswordWordDirection,
        questionsToInstruction: MutableSet<CrosswordQuestion>
    ): CrosswordQuestion? {
        return if (checkIfWordFits(
                wordToFit = aiGeneratedQuestion.word,
                startCoordinates = startCoordinates,
                direction = direction
            )
        ) {
            place(
                aiGeneratedQuestion = aiGeneratedQuestion,
                startCoordinates = startCoordinates,
                direction = direction,
                questionsToInstruction = questionsToInstruction
            )
        } else null
    }

    /**
     * Convert board from mutable list to an immutable list.
     */
    fun toList(): List<List<String?>> {
        return trim().map { it.toList() }
    }

    /**
     * Trim the board by removing leading empty rows and columns from top, bottom, left, and right.
     */
    private fun trim(): MutableList<MutableList<String?>> {
        val cellsCopy = this.cells.toMutableList()

        // Trim null-filled rows from the top
        while (cellsCopy.isNotEmpty() && cellsCopy.first().all { it == null }) {
            this.cells.removeAt(0)
        }

        // Trim null-filled rows from the bottom
        while (cellsCopy.isNotEmpty() && cellsCopy.last().all { it == null }) {
            cellsCopy.removeAt(cellsCopy.size - 1)
        }

        // Trim null-filled columns from the left
        while (cellsCopy.isNotEmpty() && cellsCopy.all { it.first() == null }) {
            cellsCopy.forEach { it.removeAt(0) }
        }

        // Trim null-filled columns from the right
        while (cellsCopy.isNotEmpty() && cellsCopy.all { it.last() == null }) {
            cellsCopy.forEach { it.removeAt(it.size - 1) }
        }

        return cellsCopy
    }
}
