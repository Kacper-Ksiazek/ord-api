package com.backend.ord.api.requests.openai

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.exceptions.REST.BadGatewayException
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
            max_tokens = openAIProperties.maxTokens,
            messages = arrayOf(
                ChatGPTMessage(
                    role = ChatGPTRole.user,
                    content = prompt
                ),
                ChatGPTMessage(
                    role = ChatGPTRole.assistant,
                    content = context
                )
            )
        )
    }

    fun createRequestWithoutContext(prompt: String): OpenAIRequest {
        return OpenAIRequest(
            model = openAIProperties.gptModel,
            temperature = openAIProperties.temperature,
            max_tokens = openAIProperties.maxTokens,
            messages = arrayOf(
                ChatGPTMessage(
                    role = ChatGPTRole.user,
                    content = prompt
                )
            )
        )
    }

    fun trackOpenAIAPIRequestAttempt(attempt: Int) {
        if (attempt > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
            throw BadGatewayException("The maximum number of OpenAI API request attempts has been reached.")
        }
    }
}


