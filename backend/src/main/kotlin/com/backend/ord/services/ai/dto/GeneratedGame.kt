package com.backend.ord.services.ai.dto

import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedWordsTyping

data class GeneratedGame<AIResponse, ProperAnswers>(
    val aiResponse: AIResponse,
    val properAnswers: ProperAnswers
)

typealias GeneratedCrosswordGame = GeneratedGame<AIGeneratedCrossword, CrosswordProperAnswers>

typealias GeneratedWordsTypingGame = GeneratedGame<AIGeneratedWordsTyping, WordsTypingProperAnswers>