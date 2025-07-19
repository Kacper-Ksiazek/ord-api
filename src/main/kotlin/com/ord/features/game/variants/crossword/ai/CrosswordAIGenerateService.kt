package com.ord.features.game.variants.crossword.ai

import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.variants.crossword.ai.dto.AIGeneratedCrossword
import com.ord.features.game.variants.crossword.ai.dto.GeneratedCrosswordGame
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.shared.prompts.AvailablePrompts
import org.springframework.stereotype.Service

@Service
class CrosswordAIGenerateService() : AIGenerateGameServiceBase<
        GeneratedCrosswordGame,
        AIGeneratedCrossword
        >(
    gameType = GameType.CROSSWORD,
    prompt = AvailablePrompts.GAMES_GENERATE_CROSSWORD,
    aiResponseClazz = AIGeneratedCrossword::class
) {
    override fun parseAIResponse(responseBody: AIGeneratedCrossword, context: GameContext): AIGeneratedCrossword {
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

    override fun validateAIResponse(parsedResponseBody: AIGeneratedCrossword?, context: GameContext): Boolean {
        val amountOfQuestion = context.amountOfQuestion

        return parsedResponseBody?.questions?.size == amountOfQuestion &&
                parsedResponseBody.questions.map { it.word }.distinct().size == amountOfQuestion
    }

    override fun refineAIResponse(aiResponse: AIGeneratedCrossword, context: GameContext): GeneratedCrosswordGame {
        return GeneratedCrosswordGame(
            instruction = CrosswordInstruction.Companion.construct(
                aiGeneratedQuestions = aiResponse,
                difficulty = context.difficulty
            ),
            properAnswers = CrosswordProperAnswers(
                finalWord = aiResponse.answer,
                questions = aiResponse.questions.associate { it.id to it.word }
            )
        )
    }
}