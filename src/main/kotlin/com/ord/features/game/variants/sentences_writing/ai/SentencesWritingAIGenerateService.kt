package com.ord.features.game.variants.sentences_writing.ai

import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.variants.sentences_writing.ai.dto.AIGeneratedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.ai.dto.GeneratedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.shared.prompts.AvailablePrompts
import org.springframework.stereotype.Service

@Service
class SentencesWritingAIGenerateService() : AIGenerateGameServiceBase<
        GeneratedSentencesWritingGame,
        AIGeneratedSentencesWritingGame
        >(
    gameType = GameType.SENTENCES_WRITING,
    prompt = AvailablePrompts.GAMES_GENERATE_SENTENCES_WRITING,
    aiResponseClazz = AIGeneratedSentencesWritingGame::class,
) {
    override fun parseAIResponse(
        responseBody: AIGeneratedSentencesWritingGame,
        context: GameContext
    ): AIGeneratedSentencesWritingGame {
        return context.words.associateWith {
            (responseBody[it] ?: throw BadRequestException("AI response is not valid! $it not found"))
        }
    }

    override fun validateAIResponse(
        parsedResponseBody: AIGeneratedSentencesWritingGame?,
        context: GameContext
    ): Boolean {
        return parsedResponseBody?.values?.size == context.amountOfQuestion &&
                parsedResponseBody.keys.distinct().size == context.amountOfQuestion &&
                context.words.all { parsedResponseBody.keys.contains(it) }
    }

    override fun refineAIResponse(
        aiResponse: AIGeneratedSentencesWritingGame,
        context: GameContext
    ): GeneratedSentencesWritingGame {
        val instruction = SentencesWritingInstruction(aiResponse)

        return GeneratedSentencesWritingGame(
            instruction = instruction,
            properAnswers = SentencesWritingProperAnswers(instruction),
        )
    }
}