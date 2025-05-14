package com.backend.ord.services.ai.dto.generated_games

import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.application.games.words_typing.WordsTypingQuestion
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.services.ai.dto.ai_responses.games.AIGeneratedWordsTypingData
import com.backend.ord.utils.hideLetters
import java.util.*

typealias GeneratedWordsTypingGame = GeneratedGameBase<WordsTypingInstruction, WordsTypingProperAnswers>

fun GeneratedWordsTypingGame(
    aiResponse: AIGeneratedWordsTypingData,
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
