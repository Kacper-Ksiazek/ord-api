package com.backend.ord.domain.application.games.crossword

import com.backend.ord.domain.application.games.crossword.board.Board
import com.backend.ord.domain.application.games.crossword.board.Coordinates
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedCrossword
import com.backend.ord.utils.games.hideLettersInWord
import com.backend.ord.utils.games.isHiddenChar
import com.backend.ord.utils.games.isSpecialChar
import com.backend.ord.utils.games.updateWord
import com.fasterxml.jackson.annotation.JsonIgnore

data class CrosswordInstruction(
    val answerExplanation: String,
    val answer: String,
    val questions: Set<CrosswordQuestion>,
    val board: MutableList<MutableList<String?>>,

    @JsonIgnore
    val lettersAreHidden: Boolean = false
) {
    private lateinit var finalWordUnmatchedIndexes: Set<Int>;

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
                if (isSpecialChar(finalWordLetter.value)) return@forEach

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
            answer = hideLettersInWord(
                wordToHide = this.answer,
                difficulty = difficulty,
                lettersToReveal = this.finalWordUnmatchedIndexes
            )
        )

        val revealedLettersInAnswer =
            instructionCopy.answer.withIndex().filter { !isHiddenChar(it.value) }.map { it.index }.toSet()

        // Iterate over all words placed on the board
        instructionCopy.questions.map { question ->
            // Hide letters in the word
            question.word = hideLettersInWord(
                wordToHide = question.word,
                difficulty = difficulty
            )

            // Remove final word components corresponding to revealed letters
            question.removeAnswerComponents(revealedLettersInAnswer)

            this.board.updateWord(question)
        }


        return instructionCopy
    }
}