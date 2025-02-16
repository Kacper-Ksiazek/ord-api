package com.backend.ord.domain.persistence.embedded.game_instructions

import com.backend.ord.domain.application.games.Board
import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import com.backend.ord.services.ai.dto.crossword.addAnswerComponent
import com.backend.ord.utils.games.CrosswordUtils.SPECIAL_CHARS

class CrosswordInstruction(
    val answerExplanation: String,
    val answer: String,
    val questions: Set<CrosswordQuestion>,
    val board: List<List<String?>>
) {
    companion object {
        /**
         * A factory method to construct a crossword instruction from AI-generated questions.
         */
        fun construct(
            aiGeneratedQuestions: AIGeneratedCrossword,
            boardDimension: Coordinates = Coordinates(x = 32, y = 24),
            firstWordStart: Coordinates = Coordinates(x = 5, y = 5)
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

            setFinalWordComponents(result)

            return result
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
                if (SPECIAL_CHARS.contains(finalWordLetter.value)) return@forEach

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
}