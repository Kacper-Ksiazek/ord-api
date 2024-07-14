package com.backend.ord.controllers

import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.config.RestClientConfig
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import com.backend.ord.exceptions.REST.BadRequestException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/openai")
class OpenAIAccessController(
    private val restClientConfig: RestClientConfig,
    private val OpenAIRequestFactory: OpenAIRequestFactory
) {
    @GetMapping("/examples-of-usage")
    fun index(
        @RequestParam(required = false) level: LanguageProficiencyLevel?,
        @RequestParam(required = false) language: LanguageName?,
        @RequestParam(required = false) word: String?,
        @RequestParam(defaultValue = "3") examplesCount: Int
    ): ResponseEntity<*> {
        // Validate all the parameters are not null

        if (word == null) throw BadRequestException("Missing query param: word")
        if (level == null) throw BadRequestException("Missing query param: level")
        if (language == null) throw BadRequestException("Missing query param: language")

        // Create the request
        val request = OpenAIRequestFactory.createRequest(
            prompt = String.format(
                "Generate %d example sentences in %s language with %s level of proficiency for the word \"%s\".",
                examplesCount,
                language.name,
                level.name,
                word
            ),
            context = "Generate response in JSON array format: [\"example1\", \"example2\", ...]"
        )

        // Send the request to OpenAI
        val response = restClientConfig.makeOpenAIPostRequest(request)

        // Return 200 ok code
        return ResponseEntity.ok().body(response.actualResponse)
    }
}
