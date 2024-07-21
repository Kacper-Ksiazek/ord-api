package com.backend.ord.controllers

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.config.RestClientConfig
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import com.backend.ord.utils.Console
import com.backend.ord.utils.StringUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/openai")
class OpenAIAccessController(
    private val restClientConfig: RestClientConfig,
    private val openAIRequestFactory: OpenAIRequestFactory
) {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/examples-of-usage")
    fun index(
        @RequestParam level: LanguageProficiencyLevel,
        @RequestParam language: LanguageName,
        @RequestParam word: String,
        @RequestParam(defaultValue = "3") examplesCount: Int
    ): ResponseEntity<*> {
        // Create the request
        val request = openAIRequestFactory.createRequest(
            prompt = String.format(
                "Generate %d example sentences in %s language with %s level of proficiency for the word \"%s\".",
                examplesCount,
                language.name,
                level.name,
                word
            ),
            context = "Generate response in JSON array format: [\"example1\", \"example2\", ...]. I want my answer to be suitable for any JSON parser such as Jackson or JSON.parse from js. Do not add any markdown formatting around the examples, just raw JSON.",
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(request).also {
            // Display the usage tokens consumption
            Console.printYellow("\nOpenAI request for $examplesCount examples of usage tokens consumption:\n")
            println("- Prompt: ${it.usage.prompt_tokens}")
            println("- Completion: ${it.usage.completion_tokens}")
            println("- Total: ${it.usage.total_tokens}")
        }

        val unparsedResponse: String = response.actualResponse.let {
            // Add asterisks around the word in the examples in order to highlight it
            StringUtils.addAsteriskAroundWordInText(
                text = it,
                word = word
            )
        }

        // Parse request into List<String>
        val examples: List<String> = jsonObjectMapper.readValue(unparsedResponse)

        // Return 200 ok code
        return ResponseEntity.ok().body(examples)
    }
}
