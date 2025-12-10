package com.ord.features.game.variants.words_typing.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.features.game.variants.words_typing.ai.dto.AIGeneratedWordsTypingData
import com.ord.features.game.variants.words_typing.ai.dto.GeneratedWordsTypingGame
import com.ord.features.game.variants.words_typing.ai.dto.openai.OpenAIWordsTyping
import com.ord.features.game.variants.words_typing.ai.dto.openai.toDomain
import com.ord.shared.prompts.AvailablePrompts
import org.springframework.stereotype.Service

@Service
class WordsTypingAIGenerateService : AIGenerateGameServiceBase<
        GeneratedWordsTypingGame,
        OpenAIWordsTyping,
        >(
    gameType = GameType.WORDS_TYPING,
    prompt = AvailablePrompts.GAMES_GENERATE_WORDS_TYPING,
    aiResponseTypeReference = object : TypeReference<OpenAIWordsTyping>() {},
) {
    override fun parseAIResponse(
        responseBody: OpenAIWordsTyping,
        context: GameContext
    ): OpenAIWordsTyping {
        val wordPairsMap = responseBody.wordPairs.associate { it.word to it.clue }

        val filteredPairs = context.words.map { word ->
            responseBody.wordPairs.find { it.word == word }
                ?: throw BadRequestException("AI response is not valid! $word not found")
        }

        return OpenAIWordsTyping(wordPairs = filteredPairs)
    }

    override fun validateAIResponse(
        parsedResponseBody: OpenAIWordsTyping?,
        context: GameContext
    ): Boolean {
        if (parsedResponseBody == null) return false

        val wordPairsMap = parsedResponseBody.wordPairs.associate { it.word to it.clue }

        return wordPairsMap.values.size == context.amountOfQuestion &&
                wordPairsMap.keys.distinct().size == context.amountOfQuestion &&
                context.words.all { wordPairsMap.keys.contains(it) }
    }

    override fun refineAIResponse(
        aiResponse: OpenAIWordsTyping,
        context: GameContext
    ): GeneratedWordsTypingGame {
        return GeneratedWordsTypingGame(aiResponse.toDomain())
    }
}