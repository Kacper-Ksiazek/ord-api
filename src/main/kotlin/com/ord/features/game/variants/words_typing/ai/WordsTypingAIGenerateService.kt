package com.ord.features.game.variants.words_typing.ai

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.variants.words_typing.ai.dto.AIGeneratedWordsTypingData
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForWordsTypingGame
import com.ord.features.game.variants.shared.ai.AIGenerateGameServiceBase
import com.ord.features.game.variants.words_typing.ai.dto.GeneratedWordsTypingGame
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.stereotype.Service

@Service
class WordsTypingAIGenerateService: AIGenerateGameServiceBase<GeneratedWordsTypingGame>(){
    override fun generate(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedWordsTypingGame {
        val languageProficiency: LanguageProficiencyEntity = user.getProficiencyInLanguage(language)
        val amountOfQuestion: Int = difficulty.getNumberOfWordsForWordsTypingGame()

        val words = getWordsForGame(
            user = user,
            language = language,
            n = amountOfQuestion
        ).map { it.origin }

        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_GENERATE_WORDS_TYPING,
            params = mapOf(
                "language" to language.name,
                "difficulty" to difficulty.name,
                "proficiency" to languageProficiency.proficiency.name,
                "words" to words.toParamString(tabulated = true),
                "amountOfQuestions" to amountOfQuestion.toString(),
                "generativeContentLanguage" to languageProficiency.generativeContentLanguage.toString(),
            )
        ).toString()

        val aiGeneratedWordsTypingGame = openAIAPIClientService.makeGameRequest<AIGeneratedWordsTypingData>(
            clazz = AIGeneratedWordsTypingData::class.java,

            user = user,
            prompt = prompt,
            difficulty = difficulty,
            language = language,

            gameType = GameType.WORDS_TYPING,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            validateResponseBody = { parsedResponseBody ->
                parsedResponseBody?.values?.size == amountOfQuestion &&
                        parsedResponseBody.keys.distinct().size == amountOfQuestion &&
                        words.all { parsedResponseBody.keys.contains(it) }
            },

            parseResponseBody = { rawResponseBody ->
                words.associateWith {
                    (rawResponseBody[it] ?: throw BadRequestException("AI response is not valid! $it not found"))
                }
            }
        )

        return GeneratedWordsTypingGame(aiGeneratedWordsTypingGame)
    }
}