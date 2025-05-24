package com.backend.ord.core.ai_provider.dto.factories

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.core.ai_provider.dto.OpenAIRequest
import com.backend.ord.prompts.Prompts
import org.springframework.stereotype.Component

@Component
class OpenAIRequestFactory(
    private val openAIProperties: OpenAIProperties
) {
    fun createRequest(
        prompt: String,
        context: String = Prompts.DEFAULT_CONTEXT
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