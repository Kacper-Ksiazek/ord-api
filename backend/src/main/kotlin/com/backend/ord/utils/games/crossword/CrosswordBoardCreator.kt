package com.backend.ord.utils.games.crossword

import com.backend.ord.domain.application.games.Board
import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import com.backend.ord.services.ai.dto.crossword.CrosswordWordDirection
import com.backend.ord.services.ai.dto.crossword.getCoordinatesOfLetterAtIndex

class CrosswordBoard {
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

    private fun Board.placeAllQuestions(
        questions: List<CrosswordQuestion>,
        firstWordStart: Coordinates
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

                return@placeWord false// No place found for the word
            }

            if (!wordHasBeenPlaced) {
                val longestWord = remainingWords.maxByOrNull { it.word.length }!!
                run insertSeparator@{
                    questionsToInstruction.reversed().forEach { lastInsertedWord ->
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

                            return@insertSeparator
                        }
                    }
                }
            }
        }

        return questionsToInstruction
    }
}