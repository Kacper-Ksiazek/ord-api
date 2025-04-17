package com.backend.ord.services.impl.ai

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.api.responses.openai.OpenAIResponse
import com.backend.ord.config.RestClientConfig
import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.exceptions.REST.BadGatewayException
import com.backend.ord.services.ai.OpenAIAPIClientService
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service

@Service
class OpenAIAPIClientServiceImpl(
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val restClientConfig: RestClientConfig,
    private val openAIProperties: OpenAIProperties,
    private val gameTokensUsageService: GameTokensUsageService,
) : OpenAIAPIClientService {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    override fun <T> makeGameRequest(
        clazz: Class<T>,

        user: User,
        prompt: String,
        gameType: GameType,
        difficulty: GameDifficulty,
        leadingLanguage: LanguageName,
        instructionLanguage: LanguageName,
        consumptionType: GamesGPTTokensConsumptionType,

        validateResponseBody: (parsedResponseBody: T?) -> Boolean,

        parseResponseBody: (responseBody: T) -> T,

        ): T {
        return makeRequest<T>(
            prompt = prompt,
            validateResponseBody = validateResponseBody,
            parseResponseBody = parseResponseBody,
            clazz = clazz,
            saveLog = {
                gameTokensUsageService.save(
                    user = user,

                    gameDifficulty = difficulty,
                    leadingLanguage = leadingLanguage,
                    instructionLanguage = instructionLanguage,

                    gameType = gameType,
                    consumptionType = consumptionType,

                    inputTokens = it.usage.input_tokens,
                    outputTokens = it.usage.output_tokens
                )
            },
        )
    }

    private fun <T> makeRequest(
        clazz: Class<T>,

        prompt: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T = { it }
    ): T {
        val openAIRequest = openAIRequestFactory.createRequest(prompt)

        var response: OpenAIResponse
        var parsedResponseBody: T?

        var numberOfAttempts = 0

        do {
            trackOpenAIAPIRequestAttempt(numberOfAttempts++)

            response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
                saveLog(it)
            }

            parsedResponseBody = try {
                parseResponseBody(jsonObjectMapper.readValue(response.data, clazz))
            } catch (_: Exception) {
                null
            }
        } while (parsedResponseBody === null || !validateResponseBody(parsedResponseBody))

        return parsedResponseBody
    }

    private fun trackOpenAIAPIRequestAttempt(attempt: Int) {
        if (attempt > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
            throw BadGatewayException("AI service could not generate a valid response after $attempt attempts. Please try again.")
        }
    }
}