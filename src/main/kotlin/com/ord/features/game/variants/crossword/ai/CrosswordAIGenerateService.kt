package com.ord.features.game.variants.crossword.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.variants.crossword.ai.dto.GeneratedCrosswordGame
import com.ord.features.game.variants.crossword.ai.dto.openai.OpenAICrossword
import com.ord.features.game.variants.crossword.ai.dto.openai.toDomain
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.shared.prompts.AvailablePrompts
import org.springframework.stereotype.Service

@Service
class CrosswordAIGenerateService() : AIGenerateGameServiceBase<
        GeneratedCrosswordGame,
        OpenAICrossword
        >(
    gameType = GameType.CROSSWORD,
    prompt = AvailablePrompts.GAMES_GENERATE_CROSSWORD,
    aiResponseTypeReference = object : TypeReference<OpenAICrossword>() {},
) {

    override fun parseAIResponse(
        responseBody: OpenAICrossword,
        context: GameContext
    ): OpenAICrossword {
        val expectedNumberOfQuestions = context.amountOfQuestion

        return responseBody.copy(
            questions = with(responseBody.questions) {
                if (size > expectedNumberOfQuestions) {
                    shuffled().take(expectedNumberOfQuestions)
                } else {
                    this
                }
            }
        )
    }

    override fun validateAIResponse(
        parsedResponseBody: OpenAICrossword?,
        context: GameContext
    ): Boolean {
        val amountOfQuestion = context.amountOfQuestion

        return parsedResponseBody?.questions?.size == amountOfQuestion &&
                parsedResponseBody.questions.map { it.word }.distinct().size == amountOfQuestion
    }

    override fun refineAIResponse(
        aiResponse: OpenAICrossword,
        context: GameContext
    ): GeneratedCrosswordGame {
        val domainResponse = aiResponse.toDomain()

        return GeneratedCrosswordGame(
            instruction = CrosswordInstruction.Companion.construct(
                aiGeneratedQuestions = domainResponse,
                difficulty = context.difficulty
            ),
            properAnswers = CrosswordProperAnswers(
                finalWord = domainResponse.answer,
                questions = domainResponse.questions.associate { it.id to it.word }
            )
        )
    }
}