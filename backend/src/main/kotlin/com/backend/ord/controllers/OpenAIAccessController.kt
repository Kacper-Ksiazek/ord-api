package com.backend.ord.controllers

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.config.RestClientConfig
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import com.backend.ord.utils.Console
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
    private val openAIRequestFactory: OpenAIRequestFactory
) {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/examples-of-usage")
    fun index(
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
}
