package com.backend.ord.utils.games

import com.backend.ord.domain.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

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
    word: String,
    startCoordinates: Pair<Int, Int>,
    direction: CrosswordWordDirection
): Boolean {
    val wordSize: Int = word.length

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
    word: String,
    startCoordinates: Pair<Int, Int>,
    direction: CrosswordWordDirection
) {
    val wordSize: Int = word.length

    if (direction == CrosswordWordDirection.HORIZONTAL) {
        val x = startCoordinates.first
        val y = startCoordinates.second

        for (i in 1 until wordSize) {
            this[y][x + i] = word[i].toString()
        }
    } else {
        val x = startCoordinates.first
        val y = startCoordinates.second

        for (i in 1 until wordSize) {
            this[y + i][x] = word[i].toString()
        }
    }
}

private fun MutableList<MutableList<String?>>.insertIfFits(
    word: String,
    startCoordinates: Pair<Int, Int>,
    direction: CrosswordWordDirection
): Boolean {
    if (this.checkIfWordFits(word, startCoordinates, direction)) {
        this.insertWord(word, startCoordinates, direction)
        return true
    }

    return false
}

object CrosswordUtils {
    fun createBoard(
        aiGeneratedQuestions: AIGeneratedCrossword,
        difficulty: GameDifficulty,
        boardSizeX: Int = 32,
        boardSizeY: Int = 24
    ): MutableList<MutableList<String?>> {
        // This is a set of questions that will be returned as a final instruction's component
        val questionsToInstruction: MutableSet<CrosswordQuestion> = mutableSetOf()

        // Create a board with the given dimensions
        val board: MutableList<MutableList<String?>> = MutableList(boardSizeY) {
            MutableList(boardSizeX) { null }
        }

        // Prepare a list of remaining words to be placed on the board
        val remainingWords: MutableList<AIGeneratedCrosswordQuestion> = aiGeneratedQuestions.questions.toMutableList()

        // Fill the board with the questions forming a crossword puzzle
        // Get the initial question
        val initialQuestion = remainingWords.pickRandomQuestionAndRemove()

        // TODO: A while loop to fill the board with the questions
        // - Pick a random question from the remaining questions
        // - Pick a random letter from the last word placed on the board as a start position for a new word
        // - If no word can be placed, then drawn new word from the remaining words

        return board;
    }
}