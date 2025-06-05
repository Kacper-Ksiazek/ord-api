package com.backend.ord.features.game.ai.generate.dto

import com.backend.ord.features.game.ai.generate.llm_api_responses.AIGeneratedWordsTypingData
import com.backend.ord.features.game.model.ongoing_game.json.WordsTypingProperAnswers
import com.backend.ord.features.game.variants.words_typing.dto.WordsTypingInstruction
import com.backend.ord.features.game.variants.words_typing.dto.WordsTypingQuestion
import com.backend.ord.shared.utils.hideLetters
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
