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
        CrosswordWordDirection.HORIZONTAL -> Pair(this.first - offset, this.second)
        CrosswordWordDirection.VERTICAL -> Pair(this.first, this.second - offset)
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

        // Keep drawing words until there are no more words to draw
        while (remainingWords.isNotEmpty()) {
            var wordHasBeenPlaced = false
            val drawnWord = remainingWords.pickRandomQuestion();

            // Iterate over the board from the last inserted word
            for (previousQuestion in questionsToInstruction.reversed()) {
                // Prepare a list of Pair word's letter and its index in the word string
                val wordLetters: List<IndexedValue<Char>> = previousQuestion.word.withIndex().toList().shuffled()

                // New word should be perpendicular to the last inserted word
                val directionInWhichToInsert: CrosswordWordDirection = previousQuestion.direction.opposite()

                // Find a place to insert the word
                for (previousQuestionLetter in wordLetters) {
                    // Find all the indexes of the letter in the drawn word
                    val commonLetters: List<IndexedValue<Char>> = drawnWord.word
                        .toCharArray()
                        .withIndex()
                        .filter { it.value == previousQuestionLetter.value }

                    if (commonLetters.isEmpty()) {
                        continue
                    }

                    for (commonLetter in commonLetters) {
                        // Establish the starting position of the new word by shifting it by the common letter's index
                        val startingPosition: Pair<Int, Int> = previousQuestion
                            .getCoordinatesOfLetterAtIndex(
                                index = previousQuestionLetter.index
                            )
                            .shiftInDirection(
                                direction = directionInWhichToInsert,
                                offset = commonLetter.index
                            )


                        wordHasBeenPlaced = board.insertIfFits(
                            question = drawnWord,
                            startCoordinates = startingPosition,
                            direction = directionInWhichToInsert,
                            questionsToInstruction = questionsToInstruction
                        ) != null

                        if (wordHasBeenPlaced) {
                            remainingWords.removeQuestion(drawnWord)
                            break
                        }
                    }
                    if (wordHasBeenPlaced) break
                }

                if (wordHasBeenPlaced) break
            }

            // If no word has been placed, then add a separator and add a word after it
            if (!wordHasBeenPlaced) {
                // Get the end position of the last inserted word
                val lastInsertedWord = questionsToInstruction.last()

                // Find the longest word in the remaining words
                val longestWord: AIGeneratedCrosswordQuestion = remainingWords.maxByOrNull { it.word.length }!!

                // TODO: Validate whether the longest word fits on the board
                val separatorCoordinates: Pair<Int, Int> = when (lastInsertedWord.direction) {
                    CrosswordWordDirection.HORIZONTAL -> Pair(
                        lastInsertedWord.endCoordinates.first + 1,
                        lastInsertedWord.endCoordinates.second
                    )

                    CrosswordWordDirection.VERTICAL -> Pair(
                        lastInsertedWord.endCoordinates.first,
                        lastInsertedWord.endCoordinates.second + 1
                    )
                }

                // Insert a new word after the separator
                val coordinatesOfWordToInsert = when (lastInsertedWord.direction) {
                    CrosswordWordDirection.HORIZONTAL -> Pair(
                        separatorCoordinates.first + 1,
                        separatorCoordinates.second
                    )

                    CrosswordWordDirection.VERTICAL -> Pair(
                        separatorCoordinates.first,
                        separatorCoordinates.second + 1
                    )
                }

                board.insertIfFits(
                    question = longestWord,
                    startCoordinates = coordinatesOfWordToInsert,
                    direction = directionOfLastInsertedWord.opposite(),
                    questionsToInstruction = questionsToInstruction
                )?.let {
                    remainingWords.removeQuestion(longestWord)
                    // Insert the separator
                    board[separatorCoordinates.second][separatorCoordinates.first] = Symbols.SEPARATOR
                }

                //TODO: If this word cannot be placed, then add a separator and try again
            }
        }


        return board;
    }
}