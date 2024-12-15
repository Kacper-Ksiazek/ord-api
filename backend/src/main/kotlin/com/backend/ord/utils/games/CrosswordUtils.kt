package com.backend.ord.utils.games

import com.backend.ord.domain.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

private object Symbols {
    const val SEPARATOR = "__SEPARATOR"
}

private fun CrosswordWordDirection.opposite(): CrosswordWordDirection {
    return if (this == CrosswordWordDirection.HORIZONTAL) {
        CrosswordWordDirection.VERTICAL
    } else {
        CrosswordWordDirection.HORIZONTAL
    }
}

private fun CrosswordQuestion.getCoordinatesOfLetterAtIndex(index: Int): Pair<Int, Int> {
    val (x, y) = this.startCoordinates

    return when (this.direction) {
        CrosswordWordDirection.HORIZONTAL -> Pair(x + index, y)
        CrosswordWordDirection.VERTICAL -> Pair(x, y + index)
    }
}

private fun Pair<Int, Int>.shiftInDirection(
    direction: CrosswordWordDirection,
    offset: Int
): Pair<Int, Int> {
    return when (direction) {
        CrosswordWordDirection.HORIZONTAL -> Pair(this.first + offset, this.second)
        CrosswordWordDirection.VERTICAL -> Pair(this.first, this.second + offset)
    }
}

private fun MutableList<AIGeneratedCrosswordQuestion>.pickRandomQuestion(): AIGeneratedCrosswordQuestion {
    val randomIndex = (0 until this.size).random()
    return this[randomIndex]
}

private fun MutableList<AIGeneratedCrosswordQuestion>.pickRandomQuestionAndRemove(): AIGeneratedCrosswordQuestion {
    val randomIndex = (0 until this.size).random()
    return this.removeAt(randomIndex)
}

private fun MutableList<AIGeneratedCrosswordQuestion>.removeQuestion(question: AIGeneratedCrosswordQuestion) {
    this.remove(question)
}

private fun MutableList<MutableList<String?>>.checkIfWordFits(
    question: AIGeneratedCrosswordQuestion,
    startCoordinates: Pair<Int, Int>,
    direction: CrosswordWordDirection
): Boolean {
    val wordSize: Int = question.word.length

    val x = startCoordinates.first
    val y = startCoordinates.second

    if (x < 0 || y < 0) {
        return false
    }

    if (direction == CrosswordWordDirection.HORIZONTAL) {
        // Check if the word fits horizontally - meaning if it fits within the board's width
        if (x + wordSize > this[0].size) {
            return false
        }

        // Check all the cells in the row where the word is supposed to be placed without the one cell in which the word starts
        for (i in 0 until wordSize) {
            val boardValueAtGivenLocation: String? = this[y][x + i]
            val questionValueAtGivenIndex: String = question.word[i].toString()

            if (this[y][x + i] != null && boardValueAtGivenLocation != questionValueAtGivenIndex) {
                return false
            }
        }
    } else {
        // Check if the word fits vertically - meaning if it fits within the board's height
        if (y + wordSize > this.size) {
            return false
        }

        for (i in 0 until wordSize) {
            val boardValueAtGivenLocation: String? = this[y + i][x]
            val questionValueAtGivenIndex: String = question.word[i].toString()

            if (this[y + i][x] != null && boardValueAtGivenLocation != questionValueAtGivenIndex) {
                return false
            }
        }
    }

    return true
}

private fun MutableList<MutableList<String?>>.insertWord(
    question: AIGeneratedCrosswordQuestion,
    startCoordinates: Pair<Int, Int>,
    direction: CrosswordWordDirection,
    questionsToInstruction: MutableSet<CrosswordQuestion>
): CrosswordQuestion {
    val wordSize: Int = question.word.length
    val endCoordinates: Pair<Int, Int>;

    if (direction == CrosswordWordDirection.HORIZONTAL) {
        val x = startCoordinates.first
        val y = startCoordinates.second

        for (i in 0 until wordSize) {
            this[y][x + i] = question.word[i].toString()
        }

        endCoordinates = Pair(x + wordSize - 1, y)
    } else {
        val x = startCoordinates.first
        val y = startCoordinates.second

        for (i in 0 until wordSize) {
            this[y + i][x] = question.word[i].toString()
        }

        endCoordinates = Pair(x, y + wordSize - 1)
    }

    val result = CrosswordQuestion(
        word = question.word,
        clue = question.clue,
        direction = direction,
        endCoordinates = endCoordinates,
        startCoordinates = startCoordinates,
    )

    questionsToInstruction.add(result)

    return result
}

private fun MutableList<MutableList<String?>>.insertIfFits(
    question: AIGeneratedCrosswordQuestion,
    startCoordinates: Pair<Int, Int>,
    direction: CrosswordWordDirection,
    questionsToInstruction: MutableSet<CrosswordQuestion>
): CrosswordQuestion? {
    if (this.checkIfWordFits(question, startCoordinates, direction)) {
        return this.insertWord(
            question = question,
            startCoordinates = startCoordinates,
            direction = direction,
            questionsToInstruction = questionsToInstruction
        )
    }

    return null
}

private fun MutableList<MutableList<String?>>.trim(): MutableList<MutableList<String?>> {
    // Trim null-filled rows from the top
    while (this.isNotEmpty() && this.first().all { it == null }) {
        this.removeAt(0)
    }

    // Trim null-filled rows from the bottom
    while (this.isNotEmpty() && this.last().all { it == null }) {
        this.removeAt(this.size - 1)
    }

    // Trim null-filled columns from the left
    while (this.isNotEmpty() && this.all { it.first() == null }) {
        this.forEach { it.removeAt(0) }
    }

    // Trim null-filled columns from the right
    while (this.isNotEmpty() && this.all { it.last() == null }) {
        this.forEach { it.removeAt(it.size - 1) }
    }

    return this
}

object CrosswordUtils {
    fun createBoard(
        aiGeneratedQuestions: AIGeneratedCrossword,
        boardSizeX: Int = 32,
        boardSizeY: Int = 24,
        firstWordStartingCoordinates: Pair<Int, Int> = Pair(5, 5)
    ): MutableList<MutableList<String?>> {
        // This is a set of questions that will be returned as a final instruction's component
        val questionsToInstruction: MutableSet<CrosswordQuestion> = mutableSetOf()

        // Create a board with the given dimensions
        //
        // Board is a matrix representing a crossword gameplay area
        // Each cell contains either a single letter or null
        // Additionally to the above, there are also special values
        // - "SEPARATOR" - meaning the cell is a separator between two separate questions
        val board: MutableList<MutableList<String?>> = MutableList(boardSizeY) {
            MutableList(boardSizeX) { null }
        }

        // Prepare a list of remaining words to be placed on the board
        var directionOfLastInsertedWord: CrosswordWordDirection = CrosswordWordDirection.HORIZONTAL
        val remainingWords: MutableList<AIGeneratedCrosswordQuestion> = aiGeneratedQuestions.questions.toMutableList()

        // Fill the board with the questions forming a crossword puzzle
        board.insertWord(
            question = remainingWords.pickRandomQuestionAndRemove(),
            startCoordinates = firstWordStartingCoordinates,
            direction = directionOfLastInsertedWord,
            questionsToInstruction = questionsToInstruction
        )

        // Keep drawing words until there are no more words to draw
        while (remainingWords.isNotEmpty()) {
            val drawnWord = remainingWords.pickRandomQuestion()

            val wordHasBeenPlaced = run placeWord@{
                questionsToInstruction.reversed().forEach { previousQuestion ->
                    val wordLetters = previousQuestion.word.withIndex().toList().shuffled()
                    val directionInWhichToInsert = previousQuestion.direction.opposite()

                    wordLetters.forEach { previousQuestionLetter ->
                        val commonLetters = drawnWord.word
                            .toCharArray()
                            .withIndex()
                            .filter { it.value == previousQuestionLetter.value }

                        if (commonLetters.isEmpty()) return@forEach // Continue to the next letter

                        commonLetters.forEach { commonLetter ->
                            val startingPosition = previousQuestion
                                .getCoordinatesOfLetterAtIndex(previousQuestionLetter.index)
                                .shiftInDirection(directionInWhichToInsert, -commonLetter.index)

                            board.insertIfFits(
                                question = drawnWord,
                                startCoordinates = startingPosition,
                                direction = directionInWhichToInsert,
                                questionsToInstruction = questionsToInstruction
                            )?.let {
                                remainingWords.removeQuestion(drawnWord)
                                return@placeWord true // Word placed successfully
                            }
                            board.insertIfFits(
                                question = drawnWord,
                                startCoordinates = startingPosition,
                                direction = directionInWhichToInsert,
                                questionsToInstruction = questionsToInstruction
                            )?.let {
                                remainingWords.removeQuestion(drawnWord)
                                return@placeWord true // Word placed successfully
                            }
                        }
                    }
                }

                return@placeWord false// No place found for the word
            }

            if (!wordHasBeenPlaced) {
                val longestWord = remainingWords.maxByOrNull { it.word.length }!!
                run insertSeparator@{
                    questionsToInstruction.reversed().forEach { lastInsertedWord ->
                        val separatorCoordinates = lastInsertedWord.endCoordinates.shiftInDirection(
                            direction = lastInsertedWord.direction,
                            offset = 1
                        )
                        val coordinatesOfWordToInsert = separatorCoordinates.shiftInDirection(
                            direction = lastInsertedWord.direction,
                            offset = 1
                        )

                        board.insertIfFits(
                            question = longestWord,
                            startCoordinates = coordinatesOfWordToInsert,
                            direction = lastInsertedWord.direction.opposite(),
                            questionsToInstruction = questionsToInstruction
                        )?.let {
                            remainingWords.removeQuestion(longestWord)
                            return@insertSeparator
                        }
                    }
                }
            }
        }

        return board.trim();
    }
}