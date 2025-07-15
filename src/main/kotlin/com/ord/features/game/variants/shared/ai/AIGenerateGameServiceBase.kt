package com.ord.features.game.variants.shared.ai

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.service.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfQuestions
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

abstract class AIGenerateGameServiceBase<
        TGeneratedGame,
        TAIResponse : Any
        >(
    private val gameType: GameType,
    private val prompt: AvailablePrompts,
    private val aiResponseClazz: KClass<TAIResponse>,
    private val promptExtraParams: Map<String, String> = emptyMap()
) {
    abstract fun parseAIResponse(
        responseBody: TAIResponse,
        context: GameContext
    ): TAIResponse

    abstract fun validateAIResponse(
        parsedResponseBody: TAIResponse?,
        context: GameContext
    ): Boolean

    abstract fun refineAIResponse(
        aiResponse: TAIResponse,
        context: GameContext
    ): TGeneratedGame

    fun generate(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty,
    ): TGeneratedGame {
        val languageProficiency: LanguageProficiencyEntity = user.getProficiencyInLanguage(language)
        val amountOfQuestion: Int = difficulty.getNumberOfQuestions(gameType)
        val generativeContentLanguage = languageProficiency.generativeContentLanguage
        val userLanguageProficiency = languageProficiency.proficiency

        val context = GameContext(
            language = language,
            difficulty = difficulty,
            amountOfQuestion = amountOfQuestion,
            userLanguageProficiency = userLanguageProficiency,
            generativeContentLanguage = generativeContentLanguage
        )

        val words = getWordsForGame(
            user = user,
            language = language,
            n = amountOfQuestion
        )

        val prompt = Prompt(
            variant = prompt,
            params = mapOf(
                "language" to language.name,
                "difficulty" to difficulty.name,
                "proficiency" to userLanguageProficiency.name,
                "words" to words.toParamString(tabulated = true),
                "amountOfQuestions" to amountOfQuestion.toString(),
                "generativeContentLanguage" to languageProficiency.generativeContentLanguage.toString(),
            ) + promptExtraParams
        )

        val aiResponse = openAIAPIClientService.makeGameRequest<TAIResponse>(
            clazz = aiResponseClazz.java,

            user = user,
            prompt = prompt.toString(),
            difficulty = difficulty,
            language = language,

            gameType = gameType,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            validateResponseBody = { validateAIResponse(it, context) },
            parseResponseBody = { parseAIResponse(it, context) }
        )

        return refineAIResponse(aiResponse, context)
    }

    @Autowired
    protected lateinit var wordService: WordService

    @Autowired
    protected lateinit var openAIAPIClientService: OpenAIAPIClientService

    @Autowired
    protected lateinit var languageProficiencyService: LanguageProficiencyService

    protected fun UserEntity.getProficiencyInLanguage(language: LanguageName): LanguageProficiencyEntity {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
    }

    protected fun getWordsForGame(
        user: UserEntity,
        language: LanguageName,
        n: Int,
        maximumWordLength: Int? = null,
    ): List<String> {
        // TODO: Generate a list of words using AI - plans for the future far far away

        val words = wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).data
            .apply {
                if (size < n) {
                    throw BadRequestException("Not enough words to generate a game")
                }
            }
            .shuffled()

        return if (maximumWordLength != null) {
            words
                .take(2 * n)
                .filter { it.origin.length <= maximumWordLength }
                .take(n)
        } else {
            words.take(n)
        }.map { it.origin }
    }
}