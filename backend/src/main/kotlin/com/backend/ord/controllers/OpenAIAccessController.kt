package com.backend.ord.controllers

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.config.RestClientConfig
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.LanguageProficiencyService
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
    private val languageProficiencyService: LanguageProficiencyService
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
        val user = jwtService.getAuthenticatedUser(request)!!

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
        @RequestParam(name = "translateTo") receivedTranslateToLanguage: LanguageName?
    ): ResponseEntity<*> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val translateTo: LanguageName = receivedTranslateToLanguage ?: user.nativeLanguage

        val userProficiencyInRequestedLanguage =
            languageProficiencyService.findUserProficiencyInLanguage(user.id, originalLanguage)
                ?: throw BadRequestException("User does not have any proficiency in the requested language.")

        // Create the request
        val openAIRequest = openAIRequestFactory.createRequest(
            prompt = """
                Response as a foreign language tutor. Generate a manual entry for $originalLanguage "$word" at ${userProficiencyInRequestedLanguage.proficiency} proficiency level in $translateTo language.

                Generate response in JSON format matching following typescript interface:

                type response = {
                translation: string,
                definition: string, // One or two short and concise sentences in ${userProficiencyInRequestedLanguage.generativeContentLanguage}
                type: ${WordType::class.joinEnumValues(separator = " | ")},
                extraMark: null | ${WordExtraMark::class.joinEnumValues(separator = " | ")},
                useCases: string[], // Each array element is a one separate use case. Give no more than 3. Write them in ${userProficiencyInRequestedLanguage.generativeContentLanguage}
                exampleSentences: {
                	sentence: string, // Sentence in ENGLISH
                	translation: string // Sentence in POLISH
                }[]
                }

                I want my answer to be suitable for any JSON parser such as Jackson or JSON.parse from js. Do not add any markdown formatting around the examples, just raw JSON.

                Additionally:
                1. Wrap the word in the examples with asterisks to highlight it as well as the part of the translation that corresponds to the word.
                2. If neither of the enum options for extraMark is good enough, then set the value to null
                3. I want 3 example sentences.
                4. If given word was misspelled, then return exactly and only "WORD_MISSPELLED"
            """.trimIndent(),
            context =
            "I want my answer to be suitable for any JSON parser such as Jackson or JSON.parse from js. Do not add any markdown formatting around the examples, just raw JSON.",
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
            // Display the usage tokens consumption
            Console.printYellow("\nOpenAI request for word manual entry usage tokens consumption:\n")
            println("- Prompt: ${it.usage.prompt_tokens}")
            println("- Completion: ${it.usage.completion_tokens}")
            println("- Total: ${it.usage.total_tokens}")
        }

        // Parse request into List<String>
//        val examples: Set<ExampleSentence> = jsonObjectMapper.readValue(response.data)

        // Return 200 ok code
        return ResponseEntity.ok().body(response)
    }
}
