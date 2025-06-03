package com.backend.ord.core.word.api.facades.impl

import com.backend.ord.api.responses.GenerateWordManualAIResponse
import com.backend.ord.config.RestClientConfig
import com.backend.ord.core.ai_provider.dto.factories.OpenAIRequestFactory
import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.backend.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.facades.WordAIFacade
import com.backend.ord.core.word.api.requests.dto.GenerateWordManualRequest
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.service.WordTokensUsageService
import com.backend.ord.prompts.Prompts
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component

@Component
class WordAIFacadeImpl(
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val languageProficiencyService: LanguageProficiencyService,
    private val wordTokensUsageService: WordTokensUsageService,
    private val restClientConfig: RestClientConfig,
) : WordAIFacade {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    override fun generateWordManual(
        body: GenerateWordManualRequest,
        user: UserEntity
    ): GenerateWordManualAIResponse {
        val userProficiencyInRequestedLanguage: LanguageProficiencyEntity =
            languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
                ?: throw BadRequestException("User does not have any proficiency in the requested language.")

        val translateTo: LanguageName =
            body.targetLanguage ?: userProficiencyInRequestedLanguage.generativeContentLanguage
        val proficiencyLevel: LanguageProficiencyLevel =
            body.proficiencyLevel ?: userProficiencyInRequestedLanguage.proficiency

        // Create the request
        val openAIRequest = openAIRequestFactory.createRequest(
            prompt = Prompts.AIWords.generateWordManualPrompt(
                word = body.word,
                wordLanguage = body.language,
                desiredLanguage = translateTo,
                proficiency = proficiencyLevel,
                generativeContentLanguage = userProficiencyInRequestedLanguage.generativeContentLanguage
            )
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
            wordTokensUsageService.save(
                user = user,
                word = body.word,
                translatedTo = translateTo,
                translatedFrom = body.language,
                consumptionType = WordsGPTTokensConsumptionType.GENERATE_ENTIRE_MANUAL,
                inputTokens = it.usage.input_tokens,
                outputTokens = it.usage.output_tokens,
            )
        }

        return with(response.data) {
            when {
                contains("WORD_MISSPELLED") -> throw BadRequestException("The word ${body.word} in the language ${body.language} is misspelled.")
                contains("NON_EXISTENT_WORD") -> throw BadRequestException("The word ${body.word} does not exist in the language ${body.language}.")

                else -> {
                    val result = jsonObjectMapper.readValue<GenerateWordManualAIResponse>(this)
                    result.originalWord = body.word

                    return@with result
                }
            }
        }
    }
}