package com.backend.ord.controllers

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.api.responses.GenerateWordManualAIResponse
import com.backend.ord.config.RestClientConfig
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.language.LanguageProficiencyLevel
import com.backend.ord.enums.persistance.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.enums.persistance.word.WordExtraMark
import com.backend.ord.enums.persistance.word.WordType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.gpt_tokens_usage.WordTokensUsageService
import com.backend.ord.utils.Console
import com.backend.ord.utils.EnumUtils.joinEnumValues
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
@RequestMapping("/api/v1/openai")
class OpenAIAccessController(
    private val jwtService: JwtService,
    private val restClientConfig: RestClientConfig,
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val languageProficiencyService: LanguageProficiencyService,
    private val wordTokensUsageService: WordTokensUsageService,
) {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/examples-of-usage")
    fun generateExamplesOfUsage(
        request: HttpServletRequest,
        @RequestParam level: LanguageProficiencyLevel,
        @RequestParam language: LanguageName,
        @RequestParam word: String,
        @RequestParam(required = false) translateExamplesTo: LanguageName?,
        @RequestParam(defaultValue = "3") examplesCount: Int
    ): ResponseEntity<*> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        // Create the request
        val openAIRequest = openAIRequestFactory.createRequest(
            prompt = String.format(
                "Generate %d example sentences in %s language with %s level of proficiency for the word \"%s\". Moreover, for each example generate also its translation to %s language.",
                examplesCount,
                language.name,
                level.name,
                word,
                translateExamplesTo?.name ?: user.nativeLanguage
            ),
            context = "Generate response in JSON format matching following typescript interface: type response = { sentence: string, translation: string }[]. I want my answer to be suitable for any JSON parser such as Jackson or JSON.parse from js. Do not add any markdown formatting around the examples, just raw JSON. Additionally, wrap the word in the examples with asterisks to highlight it as well as the part of the translation that corresponds to the word.",
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
            // Display the usage tokens consumption
            Console.printYellow("\nOpenAI request for $examplesCount examples of usage tokens consumption:\n")
            println("- Prompt: ${it.usage.prompt_tokens}")
            println("- Completion: ${it.usage.completion_tokens}")
            println("- Total: ${it.usage.total_tokens}")
        }

        // Parse request into List<String>
        val examples: Set<ExampleSentence> = jsonObjectMapper.readValue(response.data)

        // Return 200 ok code
        return ResponseEntity.ok().body(examples)
    }

    @GetMapping("/generate-word-manual")
    fun generateWordManual(
        request: HttpServletRequest,
        @RequestParam word: String,
        @RequestParam originalLanguage: LanguageName,
        @RequestParam(name = "level") receivedProficiencyLevel: LanguageProficiencyLevel?,
        @RequestParam(name = "translateTo") receivedTranslateToLanguage: LanguageName?
    ): ResponseEntity<GenerateWordManualAIResponse> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request);

        val userProficiencyInRequestedLanguage: LanguageProficiency =
            languageProficiencyService.findUserProficiencyInLanguage(user.id, originalLanguage)
                ?: throw BadRequestException("User does not have any proficiency in the requested language.")

        val translateTo: LanguageName =
            receivedTranslateToLanguage ?: userProficiencyInRequestedLanguage.generativeContentLanguage
        val proficiencyLevel: LanguageProficiencyLevel =
            receivedProficiencyLevel ?: userProficiencyInRequestedLanguage.proficiency

        // Create the request
        val openAIRequest = openAIRequestFactory.createRequest(
            prompt = """
                Response as a foreign language tutor. Generate a manual entry for $originalLanguage "$word" at $proficiencyLevel proficiency level in $translateTo language. 
                Explain always the most common usage of the word, do not provide any rare or outdated meanings.

                Generate response in JSON format matching following typescript interface:

                type response = {
                translation: string, // Translation of the word in $translateTo. If the word is an idiom or a phrase, provide a translation that is as close as possible to the original meaning, do not translate it literally.
                definition: string, // One or two short and concise sentences in ${userProficiencyInRequestedLanguage.generativeContentLanguage}
                type: ${WordType::class.joinEnumValues(separator = " | ")},
                extraMark: null | ${WordExtraMark::class.joinEnumValues(separator = " | ")}, // Leave null if none of the options are good enough
                useCases: string[], // If word has multiple definitions, provide multiple use cases in ${userProficiencyInRequestedLanguage.generativeContentLanguage}
                exampleSentences: {
                	sentence: string, // Sentence in $originalLanguage
                	translation: string // Sentence in $translateTo
                }[] // At least 3 examples. In both languages, the word and its translation should be surrounded with single asterisks.
                }

                Additionally, return exactly:
                    - WORD_MISSPELLED if the word is misspelled
                    - NON_EXISTENT_WORD if the word does not exist in the language
            """.trimIndent(), // TODO: Try removing new lines or tabulator to reduce the average amount of used tokens
            context =
                "I want my answer to be suitable for any JSON parser such as Jackson or JSON.parse from js. Do not add any markdown formatting around the examples, just raw JSON.",
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
            // Display the usage tokens consumption
            Console.printYellow("\nOpenAI request for word manual entry usage tokens consumption:\n")
            println("Word: $word | Original language: $originalLanguage | Translate to: $translateTo | User proficiency: ${userProficiencyInRequestedLanguage.proficiency}")
            println("- Prompt: ${it.usage.prompt_tokens}")
            println("- Completion: ${it.usage.completion_tokens}")
            println("- Total: ${it.usage.total_tokens}")

            // Save the usage tokens consumption
            wordTokensUsageService.save(
                user = user,
                word = word,
                translatedTo = translateTo,
                translatedFrom = originalLanguage,
                consumptionType = WordsGPTTokensConsumptionType.GENERATE_ENTIRE_MANUAL,
                inputTokens = it.usage.prompt_tokens,
                outputTokens = it.usage.completion_tokens,
            )
        }

        with(response.data) {
            when {
                contains("WORD_MISSPELLED") -> throw BadRequestException("The word $word in the language $originalLanguage is misspelled.")
                contains("NON_EXISTENT_WORD") -> throw BadRequestException("The word $word does not exist in the language $originalLanguage.")

                else -> {
                    val result = jsonObjectMapper.readValue<GenerateWordManualAIResponse>(this);
                    result.originalWord = word

                    return ResponseEntity.ok().body(result)
                }
            }
        }
    }
}
