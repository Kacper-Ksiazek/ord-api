package com.backend.ord.utils.games

import com.backend.ord.domain.application.games.Board
import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordQuestion
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordWordDirection
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.AIGeneratedCrosswordQuestion

private fun CrosswordQuestion.getCoordinatesOfLetterAtIndex(index: Int): Coordinates {
    val (x, y) = this.coordinates.start

    return when (this.direction) {
        CrosswordWordDirection.HORIZONTAL -> Coordinates(x + index, y)
        CrosswordWordDirection.VERTICAL -> Coordinates(x, y + index)
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
        boardDimension: Coordinates = Coordinates(x = 32, y = 24),
        firstWordStart: Coordinates = Coordinates(x = 5, y = 5)
    ): Pair<CrosswordInstruction, List<List<String?>>> {
        // This is a set of questions that will be returned as a final instruction's component
        val questionsToInstruction: MutableSet<CrosswordQuestion> = mutableSetOf()

        // Create a board with the given dimensions
        val board: Board = Board(boardDimension)

        // Prepare a list of remaining words to be placed on the board
        var directionOfLastInsertedWord: CrosswordWordDirection = CrosswordWordDirection.HORIZONTAL
        val remainingWords: MutableList<AIGeneratedCrosswordQuestion> = aiGeneratedQuestions.questions.toMutableList()

        // Fill the board with the questions forming a crossword puzzle
        board.place(
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
                    val directionInWhichToInsert = previousQuestion.direction.opposite()

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

                            board.placeIfFits(
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

                return@placeWord false// No place found for the word
            }

            if (!wordHasBeenPlaced) {
                val longestWord = remainingWords.maxByOrNull { it.word.length }!!
                run insertSeparator@{
                    questionsToInstruction.reversed().forEach { lastInsertedWord ->
                        val separatorCoordinates = lastInsertedWord.coordinates.end.copyAndShift(
                            direction = lastInsertedWord.direction,
                            offset = 1
                        )
                        val coordinatesOfWordToInsert = separatorCoordinates.copyAndShift(
                            direction = lastInsertedWord.direction,
                            offset = 1
                        )

                        board.placeIfFits(
                            aiGeneratedQuestion = longestWord,
                            start = coordinatesOfWordToInsert,
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