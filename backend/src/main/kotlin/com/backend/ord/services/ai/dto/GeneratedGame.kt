package com.backend.ord.services.ai.dto

import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.application.games.words_typing.WordsTypingQuestion
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedWordsTyping
import com.backend.ord.utils.hideLetters
import java.util.*

data class GeneratedGame<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)

typealias GeneratedCrosswordGame = GeneratedGame<CrosswordInstruction, CrosswordProperAnswers>

typealias GeneratedWordsTypingGame = GeneratedGame<WordsTypingInstruction, WordsTypingProperAnswers>

fun GeneratedWordsTypingGame(
    aiResponse: AIGeneratedWordsTyping,
): GeneratedWordsTypingGame {
    val properAnswers: WordsTypingProperAnswers = aiResponse.map {
        UUID.randomUUID() to it.key
    }.toMap()

    val instruction: WordsTypingInstruction = aiResponse.entries.map {
        val id = properAnswers.entries.find { e -> e.value == it.key }!!.key

        WordsTypingQuestion(
            id = id,
            word = it.key.hideLetters(),
            clue = it.value
        )
    }

    return GeneratedWordsTypingGame(
        instruction = instruction,
        properAnswers = properAnswers
    )
}