package com.backend.ord.domain.application.games.words_typing

import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedWordsTyping
import com.backend.ord.utils.hideLetters

typealias WordsTypingInstruction = List<WordsTypingQuestion>

fun constructWordsTypingGameInstruction(
    aiResponse: AIGeneratedWordsTyping,
    properAnswers: WordsTypingProperAnswers
): WordsTypingInstruction {
    return aiResponse.entries.map {
        val id = properAnswers.entries.find { el -> el.value == it.key }!!.key

        WordsTypingQuestion(
            id = id,
            word = it.key.hideLetters(),
            clue = it.value
        )
    }
}