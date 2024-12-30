package com.backend.ord.utils.games

import com.backend.ord.domain.application.games.Board
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

private fun CrosswordWordDirection.opposite(): CrosswordWordDirection {
    return if (this == CrosswordWordDirection.HORIZONTAL) {
        CrosswordWordDirection.VERTICAL
    } else {
        CrosswordWordDirection.HORIZONTAL
    }
}

private fun CrosswordQuestion.getCoordinatesOfLetterAtIndex(index: Int): Pair<Int, Int> {
    val (x, y) = this.coordinates.start

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

object CrosswordUtils {
    // TODO: Prepare a function to hide the answers ( make some letters empty string with the likelihood depending on the difficulty )

    fun createInstruction(
        aiGeneratedQuestions: AIGeneratedCrossword,
        boardSizeX: Int = 32,
        boardSizeY: Int = 24,
        firstWordStartingCoordinates: Pair<Int, Int> = Pair(5, 5)
    ): Pair<CrosswordInstruction, List<List<String?>>> {
        // This is a set of questions that will be returned as a final instruction's component
        val questionsToInstruction: MutableSet<CrosswordQuestion> = mutableSetOf()

        // Create a board with the given dimensions
        //
        // Board is a matrix representing a crossword gameplay area
        // Each cell contains either a single letter or null
        // Additionally to the above, there are also special values
        // - "SEPARATOR" - meaning the cell is a separator between two separate questions
        val board: Board = Board(dimensions = Pair(boardSizeX, boardSizeY))

        // Prepare a list of remaining words to be placed on the board
        var directionOfLastInsertedWord: CrosswordWordDirection = CrosswordWordDirection.HORIZONTAL
        val remainingWords: MutableList<AIGeneratedCrosswordQuestion> = aiGeneratedQuestions.questions.toMutableList()

        // Fill the board with the questions forming a crossword puzzle
        board.place(
            aiGeneratedQuestion = remainingWords.pickRandomQuestionAndRemove(),
            startCoordinates = firstWordStartingCoordinates,
            direction = directionOfLastInsertedWord,
            questionsToInstruction = questionsToInstruction
        )

        // Keep drawing words until there are no more words to draw
        while (remainingWords.isNotEmpty()) {
            val drawnWord = remainingWords.pickRandomQuestion()

            val wordHasBeenPlaced: Boolean = run placeWord@{
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

                            board.placeIfFits(
                                aiGeneratedQuestion = drawnWord,
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
                        val separatorCoordinates = lastInsertedWord.coordinates.end.shiftInDirection(
                            direction = lastInsertedWord.direction,
                            offset = 1
                        )
                        val coordinatesOfWordToInsert = separatorCoordinates.shiftInDirection(
                            direction = lastInsertedWord.direction,
                            offset = 1
                        )

                        board.placeIfFits(
                            aiGeneratedQuestion = longestWord,
                            startCoordinates = coordinatesOfWordToInsert,
                            direction = lastInsertedWord.direction.opposite(),
                            questionsToInstruction = questionsToInstruction
                        )?.let {
                            remainingWords.removeQuestion(longestWord)

                            board.insertSeparator(separatorCoordinates)

                            return@insertSeparator
                        }
                    }
                }
            }
        }

        return Pair(
            CrosswordInstruction(
                answer = aiGeneratedQuestions.answer,
                answerExplanation = aiGeneratedQuestions.answerExplanation,
                questions = questionsToInstruction
            ),
            board.toList()
        )
    }
}