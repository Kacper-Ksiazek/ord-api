package com.backend.ord.domain.application.games

import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

/**
 * Represents a matrix for a crossword gameplay area.
 *
 * Each cell can contain one of the following:
 * - A single letter
 * - `null` if the cell is empty
 * - Special values, such as:
 *   - `"SEPARATOR"`: Indicates the cell is a separator between two separate questions.
 */
class Board {
    private val cells: MutableList<MutableList<String?>>


    constructor(dimensions: Coordinates) {
        cells = MutableList(dimensions.y) { MutableList(dimensions.x) { null } }
    }

    companion object {
        object Symbols {
            const val SEPARATOR = "__SEPARATOR"
        }
    }

    fun insertSeparator(coordinates: Coordinates) {
        ensureCoordinatesFitsBoard(coordinates)

        this.cells[coordinates.y][coordinates.x] = Symbols.SEPARATOR
    }

    fun insertCharacter(
        x: Int,
        y: Int,
        character: Char
    ) {
        ensureCoordinatesFitsBoard(x, y)

        this.cells[y][x] = character.toString()
    }

    /**
     * Check whether a word fits on the board at the given coordinates and direction.
     */
    fun checkIfWordFits(
        wordToFit: String,
        start: Coordinates,
        direction: CrosswordWordDirection
    ): Boolean {
        ensureCoordinatesFitsBoard(start)

        val wordSize: Int = wordToFit.length
        val wordCoordinates: Coordinates = start.copy()

        return (0 until wordSize).all { i ->
            val letterFits = checkIfCharacterFits(
                coordinates = wordCoordinates,
                character = wordToFit[i]
            )

            wordCoordinates.shift(offset = 1, direction = direction)

            letterFits
        }
    }

    /**
     * Place a word on the board at the given coordinates and direction and add it to the list of questions.
     */
    fun place(
        start: Coordinates,
        direction: CrosswordWordDirection,
        aiGeneratedQuestion: AIGeneratedCrosswordQuestion,
        questionsToInstruction: MutableSet<CrosswordQuestion>
    ): CrosswordQuestion {
        ensureCoordinatesFitsBoard(start)

        val wordSize: Int = aiGeneratedQuestion.word.length

        if (direction == CrosswordWordDirection.HORIZONTAL) {
            for (i in 0 until wordSize) {
                insertCharacter(
                    x = start.x + i,
                    y = start.y,
                    character = aiGeneratedQuestion.word[i]
                )
            }

        } else {
            for (i in 0 until wordSize) {
                insertCharacter(
                    x = start.x,
                    y = start.y + i,
                    character = aiGeneratedQuestion.word[i]
                )
            }
        }

        val endCoordinates: Coordinates = start.copy().shift(
            offset = wordSize - 1,
            direction = direction
        )

        val result = CrosswordQuestion(
            id = aiGeneratedQuestion.id,
            word = aiGeneratedQuestion.word,
            clue = aiGeneratedQuestion.clue,
            direction = direction,
            coordinates = WordPlacementRange(
                start = start,
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
        start: Coordinates,
        direction: CrosswordWordDirection,
        questionsToInstruction: MutableSet<CrosswordQuestion>
    ): CrosswordQuestion? {
        return try {
            if (checkIfWordFits(
                    wordToFit = aiGeneratedQuestion.word,
                    start = start,
                    direction = direction
                )
            ) {
                place(
                    aiGeneratedQuestion = aiGeneratedQuestion,
                    start = start,
                    direction = direction,
                    questionsToInstruction = questionsToInstruction
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Trim the board by removing leading empty rows and columns from top, bottom, left, and right.
     */
    fun trim(instruction: CrosswordInstruction): List<List<String?>> {
        val cellsCopy = this.cells.toMutableList()

        var amountOfCellsRemovedFromTop: Int = 0
        var amountOfCellsRemovedFromLeft: Int = 0

        // Trim null-filled rows from the top
        while (cellsCopy.isNotEmpty() && cellsCopy.first().all { it == null }) {
            cellsCopy.removeAt(0)
            amountOfCellsRemovedFromTop++
        }

        // Trim null-filled rows from the bottom
        while (cellsCopy.isNotEmpty() && cellsCopy.last().all { it == null }) {
            cellsCopy.removeAt(cellsCopy.size - 1)
        }

        // Trim null-filled columns from the left
        while (cellsCopy.isNotEmpty() && cellsCopy.all { it.first() == null }) {
            cellsCopy.forEach { it.removeAt(0) }
            amountOfCellsRemovedFromLeft++
        }

        // Trim null-filled columns from the right
        while (cellsCopy.isNotEmpty() && cellsCopy.all { it.last() == null }) {
            cellsCopy.forEach { it.removeAt(it.size - 1) }
        }

        println("About to make an attempt of shifting 2d")
        // Shift the questions' coordinates to match the trimmed board
        if (amountOfCellsRemovedFromTop > 0 || amountOfCellsRemovedFromLeft > 0) {
            instruction.questions.forEach {
                it.coordinates.shift2D(
                    verticalOffset = -amountOfCellsRemovedFromTop,
                    horizontalOffset = -amountOfCellsRemovedFromLeft
                )
            }
        }

        return cellsCopy.map { it.toList() }
    }

    private fun checkIfCharacterFits(
        x: Int,
        y: Int,
        character: Char
    ): Boolean {
        // Check if the coordinates are within the board's bounds
        if (x < 0 || y < 0 || x >= this.cells[0].size || y >= this.cells.size) {
            return false
        }

        val cellValue: String? = this.cells[y][x]

        return cellValue == null || cellValue == character.toString()
    }

    private fun checkIfCharacterFits(
        coordinates: Coordinates,
        character: Char
    ): Boolean {
        return checkIfCharacterFits(
            x = coordinates.x,
            y = coordinates.y,
            character = character
        )
    }

    private fun ensureCoordinatesFitsBoard(coordinates: Coordinates) {
        ensureCoordinatesFitsBoard(coordinates.x, coordinates.y)
    }

    private fun ensureCoordinatesFitsBoard(x: Int, y: Int) {
        require(x < this.cells[0].size) { "x must be less than the board's width" }
        require(y < this.cells.size) { "y must be less than the board's height" }
    }

    fun print() {
        print('+')
        repeat(this.cells[0].size) { print("-") }
        println('+')
        this.cells.forEach { row ->
            print("|")
            row.forEach { cell ->
                print("${cell ?: ' '} ")
            }
            print("|")
            println()
        }
        print('+')
        repeat(this.cells[0].size) { print("-") }
        println('+')
    }
}
