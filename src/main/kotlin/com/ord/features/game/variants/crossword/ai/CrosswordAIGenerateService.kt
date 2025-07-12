package com.ord.features.game.variants.crossword.ai

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.ai.generate.llm_api_responses.AIGeneratedCrosswordData
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForCrossword
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.variants.crossword.ai.dto.GeneratedCrosswordGame
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.stereotype.Service

@Service
class CrosswordAIGenerateService : AIGenerateGameServiceBase<GeneratedCrosswordGame>() {
    override fun generate(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedCrosswordGame {
        val languageProficiency: LanguageProficiencyEntity = user.getProficiencyInLanguage(language)
        val amountOfQuestion: Int = difficulty.getNumberOfWordsForCrossword()

        val words = getWordsForGame(
            user = user,
            language = language,
            n = amountOfQuestion,
            maximumWordLength = 18
        ).map { it.origin }

        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_GENERATE_CROSSWORD,
            params = mapOf(
                "language" to language.name,
                "difficulty" to difficulty.name,
                "proficiency" to languageProficiency.proficiency.name,
                "words" to words.toParamString(tabulated = true),
                "amountOfQuestions" to amountOfQuestion.toString(),
            )
        ).toString()


        val aiGeneratedCrossword = openAIAPIClientService.makeGameRequest<AIGeneratedCrosswordData>(
            clazz = AIGeneratedCrosswordData::class.java,

            user = user,
            prompt = prompt,
            difficulty = difficulty,
            language = language,

            gameType = GameType.CROSSWORD,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            validateResponseBody = { parsedResponseBody ->
                parsedResponseBody?.questions?.size == amountOfQuestion &&
                        parsedResponseBody.questions.map { it.word }.distinct().size == amountOfQuestion
            },

            parseResponseBody = { responseBody ->
                responseBody.copy(
                    questions = with(responseBody.questions) {
                        if (size > difficulty.getNumberOfWordsForCrossword()) {
                            shuffled().take(difficulty.getNumberOfWordsForCrossword())
                        } else {
                            this
                        }
                    }
                )
            }
        )

        return GeneratedCrosswordGame(
            instruction = CrosswordInstruction.Companion.construct(
                aiGeneratedQuestions = aiGeneratedCrossword,
                difficulty = difficulty
            ),
            properAnswers = CrosswordProperAnswers(
                finalWord = aiGeneratedCrossword.answer,
                questions = aiGeneratedCrossword.questions.associate { it.id to it.word }
            )
        )
    }
}