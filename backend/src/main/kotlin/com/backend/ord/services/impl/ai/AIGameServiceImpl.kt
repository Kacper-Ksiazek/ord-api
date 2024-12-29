package com.backend.ord.services.impl.ai

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.openai.OpenAIRequest
import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.openai.embedded.OpenAIResponse
import com.backend.ord.config.RestClientConfig
import com.backend.ord.domain.persistance.entities.LanguageProficiency
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.domain.persistance.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistance.game.GameDifficulty
import com.backend.ord.enums.persistance.game.GameType
import com.backend.ord.enums.persistance.game.getNumberOfWordsForCrossword
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.prompts.Prompts
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service

@Service
class AIGameServiceImpl(
    private val restClientConfig: RestClientConfig,
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val wordService: WordService,
    private val languageProficiencyService: LanguageProficiencyService,
    private val gameTokensUsageService: GameTokensUsageService
) : AIGameService {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    /**
     * Generates a crossword game.
     */
    override fun generateCrosswordGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): Pair<AIGeneratedCrossword, Set<GameTokensUsage>> {
        // Store all token usage logs for the game
        val gameTokensUsageLogs: MutableSet<GameTokensUsage> = mutableSetOf()

        // Get user proficiency in the requested language
        val userProficiencyInRequestedLanguage: LanguageProficiency =
            languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, language)

        val questionsAmountBasedOnDifficulty: Int = difficulty.getNumberOfWordsForCrossword()

        // Get words for the game
        val words = wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).data
            .shuffled()
            .take(questionsAmountBasedOnDifficulty)
            .map { it.origin }

        // Prepare an API request
        val openAIRequest: OpenAIRequest = openAIRequestFactory.createRequest(
            prompt = Prompts.generateCrosswordQuestionsPrompt(
                wordsToUse = words,
                language = language,
                difficulty = difficulty,
                amountOfQuestions = questionsAmountBasedOnDifficulty,
                languageProficiency = userProficiencyInRequestedLanguage.proficiency
            )
        )

        var response: OpenAIResponse
        var parsedResponseBody: AIGeneratedCrossword?

        do {
            // Send the request to the OpenAI API
            response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
                // Log the token usage for the request
                gameTokensUsageLogs.add(
                    gameTokensUsageService.save(
                        user = user,

                        leadingLanguage = language,
                        gameDifficulty = difficulty,
                        instructionLanguage = language,

                        gameType = GameType.CROSSWORD,
                        consumptionType = GamesGPTTokensConsumptionType.GENERATE,

                        inputTokens = it.usage.prompt_tokens,
                        outputTokens = it.usage.completion_tokens
                    )
                )
            }

            // Attempt to parse the response into the expected AIGeneratedCrossword object
            parsedResponseBody = try {
                val readValue = jsonObjectMapper.readValue<AIGeneratedCrossword>(response.data)

                readValue.copy(
                    questions = with(readValue.questions) {
                        if (size > questionsAmountBasedOnDifficulty) {
                            shuffled().take(questionsAmountBasedOnDifficulty)
                        } else {
                            this
                        }
                    }
                )
            } catch (_: Exception) {
                null // Handle parsing failure by returning null
            }

            // Retry if the response doesn't have the expected number of questions
        } while (parsedResponseBody?.questions?.size != questionsAmountBasedOnDifficulty)

        // Return the validated crossword response
        return Pair(
            parsedResponseBody,
            gameTokensUsageLogs
        )
    }
}