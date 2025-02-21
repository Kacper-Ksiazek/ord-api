package com.backend.ord.domain.application.games

import com.backend.ord.exceptions.REST.BadGatewayException
import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import com.backend.ord.services.ai.dto.crossword.CrosswordWordDirection
import com.backend.ord.services.ai.dto.crossword.QuestionBoardPosition
import com.backend.ord.services.ai.dto.crossword.getCoordinatesOfLetterAtIndex

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
    private val numberOfRetries = 10
    private var cells: MutableList<MutableList<String?>>

    private val dimensions: Coordinates

    constructor(dimensions: Coordinates) {
        this.dimensions = dimensions
        this.cells = MutableList(dimensions.y) { MutableList(dimensions.x) { null } }
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
        try {
            ensureCoordinatesFitsBoard(start)
        } catch (e: Exception) {
            return false
        }

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
        aiGeneratedQuestion: CrosswordQuestion,
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
            position = QuestionBoardPosition(
                direction = direction,
                coordinates = WordPlacementRange(
                    start = start,
                    end = endCoordinates
                ),
            )

        )

        questionsToInstruction.add(result)

        return result
    }

    /**
     * Place a word on the board at the given coordinates and direction if it fits, otherwise return null.
     */
    fun placeIfFits(
        aiGeneratedQuestion: CrosswordQuestion,
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
     * Place all questions on the board, starting from the given coordinates.
     */
    fun placeAllQuestions(
        questions: List<CrosswordQuestion>,
        firstWordStart: Coordinates,
        attempt: Int = 1
    ): Set<CrosswordQuestion> {
        // This is a set of questions that will be returned as a final instruction's component
        val questionsToInstruction: MutableSet<CrosswordQuestion> = mutableSetOf()

        // Prepare a list of remaining words to be placed on the board
        var directionOfLastInsertedWord: CrosswordWordDirection = CrosswordWordDirection.HORIZONTAL
        val remainingWords: MutableList<CrosswordQuestion> = questions.toMutableList()

        // Fill the board with the questions forming a crossword puzzle
        place(
            aiGeneratedQuestion = remainingWords.pickRandomQuestionAndRemove(),
            start = firstWordStart,
            direction = directionOfLastInsertedWord,
            questionsToInstruction = questionsToInstruction
        )

        // Keep drawing words until there are no more words to draw
        while (remainingWords.isNotEmpty()) {
            val drawnWord = remainingWords.pickRandomQuestion()

            val wordHasBeenPlaced: Boolean = run placeWord@{
                questionsToInstruction.reversed().forEach { previousQuestion ->
                    val wordLetters = previousQuestion.word.withIndex().toList().shuffled()
                    val directionInWhichToInsert = previousQuestion.position!!.direction.opposite()

                    wordLetters.forEach { previousQuestionLetter ->
                        val commonLetters = drawnWord.word
                            .toCharArray()
                            .withIndex()
                            .filter { it.value == previousQuestionLetter.value }

                        if (commonLetters.isEmpty()) return@forEach // Continue to the next letter

                        commonLetters.forEach { commonLetter ->
                            val startingPosition = try {
                                previousQuestion
                                    .getCoordinatesOfLetterAtIndex(previousQuestionLetter.index)
                                    .shift(
                                        direction = directionInWhichToInsert,
                                        offset = -commonLetter.index
                                    )
                            } catch (e: Exception) {
                                return@forEach
                            }

                            placeIfFits(
                                aiGeneratedQuestion = drawnWord,
                                start = startingPosition,
                                direction = directionInWhichToInsert,
                                questionsToInstruction = questionsToInstruction
                            )?.let {
                                remainingWords.removeQuestion(drawnWord)
                                return@placeWord true // Word placed successfully
                            }
                        }
                    }
                }

                return@placeWord false // No place found for the word
            }

            if (!wordHasBeenPlaced) {
                val longestWords = remainingWords.sortedBy { it.word.length }.reversed()

                val wordHasBeenPlacedAfterInsertingSeparator: Boolean = run insertSeparator@{
                    longestWords.forEach { longestWord ->
                        questionsToInstruction.reversed().forEach { lastInsertedWord ->
                            try {
                                val direction = lastInsertedWord.position!!.direction

                                val separatorCoordinates = lastInsertedWord.position!!.coordinates.end.copyAndShift(
                                    direction = direction,
                                    offset = 1
                                )
                                val coordinatesOfWordToInsert = separatorCoordinates.copyAndShift(
                                    direction = direction,
                                    offset = 1
                                )

                                placeIfFits(
                                    aiGeneratedQuestion = longestWord,
                                    start = coordinatesOfWordToInsert,
                                    direction = direction.opposite(),
                                    questionsToInstruction = questionsToInstruction
                                )?.let {
                                    remainingWords.removeQuestion(longestWord)

                                    insertSeparator(separatorCoordinates)

                                    return@insertSeparator true
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }

                    return@insertSeparator false
                }

                if (!wordHasBeenPlacedAfterInsertingSeparator) {
                    if (attempt < numberOfRetries) {
                        //TODO: Remove this
                        println("Retrying...")

                        cells = MutableList(dimensions.y) { MutableList(dimensions.x) { null } }

                        return placeAllQuestions(
                            questions = questions,
                            firstWordStart = firstWordStart,
                            attempt = attempt + 1
                        )
                    } else {
                        throw BadGatewayException("Failed to generate a crossword puzzle")
                    }
                }
            }
        }

        return questionsToInstruction
    }

    /**
     * Trim the board by removing leading empty rows and columns from top, bottom, left, and right.
     */
    fun trim(questions: Set<CrosswordQuestion>): MutableList<MutableList<String?>> {
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

        // Shift the questions' coordinates to match the trimmed board
        if (amountOfCellsRemovedFromTop > 0 || amountOfCellsRemovedFromLeft > 0) {
            questions.forEach {
                it.position!!.coordinates.shift2D(
                    verticalOffset = -amountOfCellsRemovedFromTop,
                    horizontalOffset = -amountOfCellsRemovedFromLeft
                )
            }
        }

        return cellsCopy
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

    private fun MutableList<CrosswordQuestion>.pickRandomQuestion(): CrosswordQuestion {
        val randomIndex = (0 until this.size).random()
        return this[randomIndex]
    }

    private fun MutableList<CrosswordQuestion>.pickRandomQuestionAndRemove(): CrosswordQuestion {
        val randomIndex = (0 until this.size).random()
        return this.removeAt(randomIndex)
    }

    private fun MutableList<CrosswordQuestion>.removeQuestion(question: CrosswordQuestion) {
        this.remove(question)
    }

    // For debugging purposes only
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
