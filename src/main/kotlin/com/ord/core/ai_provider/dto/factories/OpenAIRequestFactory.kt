package com.ord.core.ai_provider.dto.factories

import com.ord.config.properties.OpenAIProperties
import com.ord.core.ai_provider.dto.OpenAIRequest
import org.springframework.stereotype.Component

@Component
class OpenAIRequestFactory(
    private val openAIProperties: OpenAIProperties
) {
    val defaultContext =
        "Do not include anything more than this JSON and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue."

    fun createRequest(
        prompt: String,
        context: String = defaultContext
    ): OpenAIRequest {
        return OpenAIRequest(
            model = openAIProperties.gptModel,
            temperature = openAIProperties.temperature,
            max_output_tokens = openAIProperties.maxTokens,
            input = prompt,
            instructions = context
        )
    }

    fun createRequestWithoutContext(prompt: String): OpenAIRequest {
        return OpenAIRequest(
            model = openAIProperties.gptModel,
            temperature = openAIProperties.temperature,
            max_output_tokens = openAIProperties.maxTokens,
            input = prompt
        )
    }
}