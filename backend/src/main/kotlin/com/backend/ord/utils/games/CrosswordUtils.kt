package com.backend.ord.utils.games

import com.backend.ord.domain.application.games.Board
import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.getNumberOfLettersToReveal
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import com.backend.ord.services.ai.dto.crossword.addAnswerComponent

object CrosswordUtils {
    const val HIDDEN_CHARACTER: Char = '*'
    val SPECIAL_CHARS: Set<Char> = setOf(' ', '\'', '-', '’', '_')


    fun createInstruction(
        aiGeneratedQuestions: AIGeneratedCrossword,
        boardDimension: Coordinates = Coordinates(x = 32, y = 24),
        firstWordStart: Coordinates = Coordinates(x = 5, y = 5)
    ): Pair<CrosswordInstruction, List<List<String?>>> {
        // Create a board with the given dimensions
        val board = Board(boardDimension)

        val questionsToInstruction = board.placeAllQuestions(
            questions = aiGeneratedQuestions.questions,
            firstWordStart = firstWordStart
        )

        val instruction = CrosswordInstruction(
            answer = aiGeneratedQuestions.answer,
            answerExplanation = aiGeneratedQuestions.answerExplanation,
            questions = questionsToInstruction
        )

        setFinalWordComponents(instruction)

        // There is a global problem here caused by setting final word components of hidden letters.
        // The order of operation here should be reversed.
        // First, set the final word components, then hide the letters in the proper answer.

        return Pair(
            instruction,
            board.trim(instruction)
        )
    }

    /**
     *
     */
    fun hideLettersInProperAnswer(
        wordToHide: String,
        difficulty: GameDifficulty
    ): String {
        val numberOfLettersToReveal: Int = difficulty.getNumberOfLettersToReveal()
        val indexesOfLettersToReveal = wordToHide.indices.shuffled().take(numberOfLettersToReveal)

        return wordToHide.mapIndexed { index, currentChar ->
            if (SPECIAL_CHARS.contains(currentChar) || indexesOfLettersToReveal.contains(index)) {
                currentChar
            } else {
                HIDDEN_CHARACTER
            }
        }.joinToString("")
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

    /**
     * Iterates over all words placed on the board and hides some of the
     * letters in them, according to the game difficulty.
     */
    private fun hideLettersAcrossCrossword(
        instruction: CrosswordInstruction,
        board: List<List<String?>>,
    ): String {
        val numberOfLettersToReveal: Int = difficulty.getNumberOfLettersToReveal()
        val indexesOfLettersToReveal = wordToHide.indices.shuffled().take(numberOfLettersToReveal)

        return wordToHide.mapIndexed { index, currentChar ->
            if (SPECIAL_CHARS.contains(currentChar) || indexesOfLettersToReveal.contains(index)) {
                currentChar
            } else {
                HIDDEN_CHARACTER
            }
        }.joinToString("")
    }
}