package com.ord.features.game.variants.words_typing.ai.dto

import com.ord.features.game.model.ongoing_game.json.WordsTypingProperAnswers
import com.ord.features.game.variants.shared.ai.dto.GeneratedGame
import com.ord.features.game.variants.words_typing.dto.WordsTypingInstruction
import com.ord.features.game.variants.words_typing.dto.WordsTypingQuestion
import com.ord.shared.utils.hideLetters
import java.util.*

typealias GeneratedWordsTypingGame = GeneratedGame<WordsTypingInstruction, WordsTypingProperAnswers>

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
