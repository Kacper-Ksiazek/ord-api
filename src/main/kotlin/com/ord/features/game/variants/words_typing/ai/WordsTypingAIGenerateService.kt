package com.ord.features.game.variants.words_typing.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.features.game.variants.words_typing.ai.dto.AIGeneratedWordsTypingData
import com.ord.features.game.variants.words_typing.ai.dto.GeneratedWordsTypingGame
import com.ord.shared.prompts.AvailablePrompts
import org.springframework.stereotype.Service

@Service
class WordsTypingAIGenerateService : AIGenerateGameServiceBase<
        GeneratedWordsTypingGame,
        AIGeneratedWordsTypingData,
        >(
    gameType = GameType.WORDS_TYPING,
    prompt = AvailablePrompts.GAMES_GENERATE_WORDS_TYPING,
    aiResponseTypeReference = object : TypeReference<AIGeneratedWordsTypingData>() {},
) {
    override fun parseAIResponse(
        responseBody: AIGeneratedWordsTypingData,
        context: GameContext
    ): AIGeneratedWordsTypingData {
        return context.words.associateWith {
            (responseBody[it] ?: throw BadRequestException("AI response is not valid! $it not found"))
        }
    }

    override fun validateAIResponse(
        parsedResponseBody: AIGeneratedWordsTypingData?,
        context: GameContext
    ): Boolean {
        return parsedResponseBody?.values?.size == context.amountOfQuestion &&
                parsedResponseBody.keys.distinct().size == context.amountOfQuestion &&
                context.words.all { parsedResponseBody.keys.contains(it) }
    }

    override fun refineAIResponse(
        aiResponse: AIGeneratedWordsTypingData,
        context: GameContext
    ): GeneratedWordsTypingGame {
        return GeneratedWordsTypingGame(aiResponse)
    }
}