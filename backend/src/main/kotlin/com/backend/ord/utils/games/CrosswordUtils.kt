package com.backend.ord.utils.games

import com.backend.ord.domain.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

private fun CrosswordWordDirection.opposite(): CrosswordWordDirection {
    return if (this == CrosswordWordDirection.HORIZONTAL) {
        CrosswordWordDirection.VERTICAL
    } else {
        CrosswordWordDirection.HORIZONTAL
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

    if (direction == CrosswordWordDirection.HORIZONTAL) {
        val x = startCoordinates.first
        val y = startCoordinates.second

        // Check if the word fits horizontally - meaning if it fits within the board's width
        if (x + wordSize > this[0].size) {
            return false
        }

        // Check all the cells in the row where the word is supposed to be placed without the one cell in which the word starts
        for (i in 1 until wordSize) {
            if (this[y][x + i] != null) {
                return false
            }
        }
    } else {
        val x = startCoordinates.first
        val y = startCoordinates.second

        // Check if the word fits vertically - meaning if it fits within the board's height
        if (y + wordSize > this.size) {
            return false
        }

        for (i in 1 until wordSize) {
            if (this[y + i][x] != null) {
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

        for (i in 1 until wordSize) {
            this[y][x + i] = question.word[i].toString()
        }

        endCoordinates = Pair(x + wordSize - 1, y)
    } else {
        val x = startCoordinates.first
        val y = startCoordinates.second

        for (i in 1 until wordSize) {
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

object CrosswordUtils {
    fun createBoard(
        aiGeneratedQuestions: AIGeneratedCrossword,
        difficulty: GameDifficulty,
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

        // TODO: A while loop to fill the board with the questions
        // - Pick a random question from the remaining questions
        // - Pick a random letter from the last word placed on the board as a start position for a new word
        // - If no word can be placed, then drawn new word from the remaining words
        // - If all words have been drawn and still no word can be placed, then add a separator word somewhere on the board and try again
        // - A word separator is described as "SEPARATOR" value on the board


        return board;
    }
}