package com.ord.features.game.variants.sentences_writing.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.variants.sentences_writing.ai.dto.generate.AIGeneratedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.ai.dto.generate.GeneratedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.ai.dto.generate.openai.OpenAISentencesWritingGame
import com.ord.features.game.variants.sentences_writing.ai.dto.generate.openai.toDomain
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.shared.prompts.AvailablePrompts
import org.springframework.stereotype.Service

@Service
class SentencesWritingAIGenerateService() : AIGenerateGameServiceBase<
        GeneratedSentencesWritingGame,
        OpenAISentencesWritingGame
        >(
    gameType = GameType.SENTENCES_WRITING,
    prompt = AvailablePrompts.GAMES_GENERATE_SENTENCES_WRITING,
    aiResponseTypeReference = object : TypeReference<OpenAISentencesWritingGame>() {},
) {
    override fun parseAIResponse(
        responseBody: OpenAISentencesWritingGame,
        context: GameContext
    ): OpenAISentencesWritingGame {
        val wordTopicsMap = responseBody.wordTopics.associate { it.word to it.topic }

        val filteredPairs = context.words.map { word ->
            responseBody.wordTopics.find { it.word == word }
                ?: throw BadRequestException("AI response is not valid! $word not found")
        }

        return OpenAISentencesWritingGame(wordTopics = filteredPairs)
    }

    override fun validateAIResponse(
        parsedResponseBody: OpenAISentencesWritingGame?,
        context: GameContext
    ): Boolean {
        if (parsedResponseBody == null) return false

        val wordTopicsMap = parsedResponseBody.wordTopics.associate { it.word to it.topic }

        return wordTopicsMap.values.size == context.amountOfQuestion &&
                wordTopicsMap.keys.distinct().size == context.amountOfQuestion &&
                context.words.all { wordTopicsMap.keys.contains(it) }
    }

    override fun refineAIResponse(
        aiResponse: OpenAISentencesWritingGame,
        context: GameContext
    ): GeneratedSentencesWritingGame {
        val domainResponse = aiResponse.toDomain()

        val instruction = SentencesWritingInstruction(domainResponse)

        return GeneratedSentencesWritingGame(
            instruction = instruction,
            properAnswers = SentencesWritingProperAnswers(instruction),
        )
    }
}