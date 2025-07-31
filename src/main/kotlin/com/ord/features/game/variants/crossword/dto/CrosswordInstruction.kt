package com.ord.features.game.variants.crossword.dto

import com.ord.features.game.variants.crossword.ai.dto.AIGeneratedCrossword
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfLettersToReveal
import com.ord.features.game.variants.crossword.dto.helpers.board.Board
import com.ord.features.game.variants.crossword.dto.helpers.board.Coordinates
import com.ord.features.game.variants.crossword.dto.helpers.board.CrosswordWordDirection
import com.ord.features.game.variants.crossword.dto.helpers.question.CrosswordQuestion
import com.ord.features.game.variants.crossword.dto.helpers.question.addAnswerComponent
import com.ord.features.game.variants.crossword.dto.helpers.question.removeAnswerComponents
import com.ord.shared.utils.hideLetters
import com.ord.shared.utils.hideLettersInWord
import com.ord.shared.utils.isHiddenChar
import com.ord.shared.utils.isSpecial
import com.fasterxml.jackson.annotation.JsonIgnore

typealias CrosswordBoard = MutableList<MutableList<String?>>

fun CrosswordBoard.updateWord(question: CrosswordQuestion) {
    val position = question.position ?: throw IllegalStateException("The question is not placed on the board yet.")

    question.word.forEachIndexed { index, letter ->
        val (x, y) = position.coordinates.start

        when (position.direction) {
            CrosswordWordDirection.HORIZONTAL -> this[y][x + index] = letter.toString()
            CrosswordWordDirection.VERTICAL -> this[y + index][x] = letter.toString()
        }
    }
}

data class CrosswordInstruction(
    val answerExplanation: String,
    val answer: String,
    val questions: Set<CrosswordQuestion>,
    val board: CrosswordBoard,

    @JsonIgnore
    val lettersAreHidden: Boolean = false
) {
    private lateinit var finalWordUnmatchedIndexes: Set<Int>

    companion object {
        /**
         * A factory method to construct a crossword instruction from AI-generated questions.
         */
        fun construct(
            aiGeneratedQuestions: AIGeneratedCrossword,
            boardDimension: Coordinates = Coordinates(x = 32, y = 24),
            firstWordStart: Coordinates = Coordinates(x = 5, y = 5),
            difficulty: GameDifficulty? = null
        ): CrosswordInstruction {
            val board = Board(boardDimension)

            val questions = board.placeAllQuestions(
                questions = aiGeneratedQuestions.questions,
                firstWordStart = firstWordStart
            )

            val result = CrosswordInstruction(
                answerExplanation = aiGeneratedQuestions.answerExplanation,
                answer = aiGeneratedQuestions.answer,
                questions = questions,
                board = board.trim(questions)
            )

            result.finalWordUnmatchedIndexes = setFinalWordComponents(result)

            return difficulty?.let { result.hideLetters(it) } ?: result
        }

        /**
         * Go through the generated crossword questions and find locations
         * of final word letters in words on the board.
         *
         * Return the set of indexes of unmatched letters - letters of the final word with no corresponding word on the board.
         */
        private fun setFinalWordComponents(
            instruction: CrosswordInstruction
        ): Set<Int> {
            val indexesOfUnmatchedLetters = mutableListOf<Int>()

            // 1. Iterate through all letters of the final word
            instruction.answer.withIndex().forEach { finalWordLetter ->
                // Skip special characters
                if (finalWordLetter.value.isSpecial()) return@forEach

                // 2. Find all words on the board that contain the letter
                val allWordsContainingLetter = instruction.questions.filter { it.word.contains(finalWordLetter.value) }

                if (allWordsContainingLetter.isEmpty()) {
                    indexesOfUnmatchedLetters.add(finalWordLetter.index)
                    return@forEach
                }

                // 3. Draw one random word from the list
                val randomWord: CrosswordQuestion = allWordsContainingLetter.random()

                // 4. Find all locations of the letter in the word
                val locationsOfLetter = randomWord.word.withIndex().filter { it.value == finalWordLetter.value }

                // 5. If there are no locations, add the index of the letter to the list of unmatched letters
                if (locationsOfLetter.isEmpty()) {
                    indexesOfUnmatchedLetters.add(finalWordLetter.index)
                    return@forEach
                }

                // 6. Pick one random location
                val randomLocation: IndexedValue<Char> = locationsOfLetter.random()

                // 6. Insert a new answer component to that word
                randomWord.addAnswerComponent(
                    indexInWord = randomLocation.index,
                    indexInPassword = finalWordLetter.index
                )
            }

            return indexesOfUnmatchedLetters.toSet()
        }
    }

    fun hideLetters(difficulty: GameDifficulty): CrosswordInstruction {
        if (this.lettersAreHidden) {
            throw IllegalAccessException("Letters are already hidden.")
        }

        val instructionCopy = this.copy(
            lettersAreHidden = true,
            answer = this.answer.hideLetters(
                lettersToReveal = this.finalWordUnmatchedIndexes,
                numberOfLettersToReveal = difficulty.getNumberOfLettersToReveal(),
            )
        )

        val revealedLettersInAnswer =
            instructionCopy.answer.withIndex().filter { !isHiddenChar(it.value) }.map { it.index }.toSet()

        // Iterate over all words placed on the board
        instructionCopy.questions.map { question ->
            // Hide letters in the word
            question.word = hideLettersInWord(
                wordToHide = question.word,
                numberOfLettersToReveal = difficulty.getNumberOfLettersToReveal(),
            )

            // Remove final word components corresponding to revealed letters
            question.removeAnswerComponents(revealedLettersInAnswer)

            this.board.updateWord(question)
        }


        return instructionCopy
    }
}