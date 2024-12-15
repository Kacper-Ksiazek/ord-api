package com.backend.ord.services.impl.ai

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.openai.OpenAIRequest
import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.openai.embedded.OpenAIResponse
import com.backend.ord.config.RestClientConfig
import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.getNumberOfWordsForCrossword
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
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
) : AIGameService {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    /**
     * Generates a crossword game.
     */
    override fun generateCrosswordGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): AIGeneratedCrossword {
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
            sortDirection = SortDirection.DESC
            // TODO: Add more filters
        ).data
            .shuffled()
            .take(questionsAmountBasedOnDifficulty)
            .map { it.origin }

        // Prepare an API request
        val openAIRequest: OpenAIRequest = openAIRequestFactory.createRequest(
            prompt = """
               Generate a foreign language practicing crossword. The game difficulty is set to $difficulty, and the foreign language is $language at $userProficiencyInRequestedLanguage proficiency level.
               
               I want my answer to match this json format.
               
               {
                 answer: string // Either a new word or a short phrase. Do not use a word from the list provided
                 answerExplanation: string // DO NOT include an answer in its explanation
                 questions: {
                   word: string // Use words for the provided list
                   clue: string
                 }[] // A list of $questionsAmountBasedOnDifficulty questions with words from the provided list
               }
               
               Words: [
                ${words.joinToString(", ") { it }}
               ]
            """.trimIndent(),
            context = """
                Do not include anything more than this json and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue.
            """.trimIndent()
        )

        // Send the request to OpenAI
        val response: OpenAIResponse = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
            // TODO: Save the usage tokens consumption
        }

        // Parse the response
        return jsonObjectMapper.readValue<AIGeneratedCrossword>(response.data)
    }
}