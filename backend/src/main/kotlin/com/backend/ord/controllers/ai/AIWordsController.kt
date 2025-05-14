package com.backend.ord.controllers.ai

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.api.responses.GenerateWordManualAIResponse
import com.backend.ord.config.RestClientConfig
import com.backend.ord.config.security.JwtService
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.prompts.Prompts
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.gpt_tokens_usage.WordTokensUsageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/ai/words")
class AIWordsController(
    private val jwtService: JwtService,
    private val restClientConfig: RestClientConfig,
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val languageProficiencyService: LanguageProficiencyService,
    private val wordTokensUsageService: WordTokensUsageService,
) {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    // TODO: Move to the POST
    @GetMapping("/generate-manual")
    fun generateWordManual(
        request: HttpServletRequest,
        @RequestParam word: String,
        @RequestParam originalLanguage: LanguageName,
        @RequestParam(name = "level") receivedProficiencyLevel: LanguageProficiencyLevel?,
        @RequestParam(name = "translateTo") receivedTranslateToLanguage: LanguageName?
    ): ResponseEntity<GenerateWordManualAIResponse> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val userProficiencyInRequestedLanguage: LanguageProficiency =
            languageProficiencyService.findUserProficiencyInLanguage(user.id, originalLanguage)
                ?: throw BadRequestException("User does not have any proficiency in the requested language.")

        val translateTo: LanguageName =
            receivedTranslateToLanguage ?: userProficiencyInRequestedLanguage.generativeContentLanguage
        val proficiencyLevel: LanguageProficiencyLevel =
            receivedProficiencyLevel ?: userProficiencyInRequestedLanguage.proficiency

        // Create the request
        val openAIRequest = openAIRequestFactory.createRequest(
            prompt = Prompts.AIWords.generateWordManualPrompt(
                word = word,
                wordLanguage = originalLanguage,
                desiredLanguage = translateTo,
                proficiency = proficiencyLevel,
                generativeContentLanguage = userProficiencyInRequestedLanguage.generativeContentLanguage
            )
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
            wordTokensUsageService.save(
                user = user,
                word = word,
                translatedTo = translateTo,
                translatedFrom = originalLanguage,
                consumptionType = WordsGPTTokensConsumptionType.GENERATE_ENTIRE_MANUAL,
                inputTokens = it.usage.input_tokens,
                outputTokens = it.usage.output_tokens,
            )
        }

        with(response.data) {
            when {
                contains("WORD_MISSPELLED") -> throw BadRequestException("The word $word in the language $originalLanguage is misspelled.")
                contains("NON_EXISTENT_WORD") -> throw BadRequestException("The word $word does not exist in the language $originalLanguage.")

                else -> {
                    val result = jsonObjectMapper.readValue<GenerateWordManualAIResponse>(this)
                    result.originalWord = word

                    return ResponseEntity.ok().body(result)
                }
            }
        }
    }
}