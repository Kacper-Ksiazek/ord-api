package com.ord.core.ai_provider.dto.factories

import com.ord.config.properties.OpenAIProperties
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate
import com.ord.core.ai_provider.dto.OpenAIRequest
import com.ord.shared.prompts.AvailableAIModels
import org.springframework.stereotype.Component

@Component
class OpenAIRequestFactory(
    private val openAIProperties: OpenAIProperties
) {
    val defaultContext =
        "Do not include anything more than this JSON and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue."

    fun createRequest(
        // TODO: Migrate to dedicated Prompt class here
        prompt: String,
        context: String = defaultContext,
        stream: Boolean = false,
        structuredOutput: StructuredOutputTemplate? = null,
    ): OpenAIRequest {
        return OpenAIRequest(
            model = AvailableAIModels.GPT_5_NANO.model,
            temperature = openAIProperties.temperature,
            max_output_tokens = openAIProperties.maxTokens,
            input = prompt,
            instructions = context,
            stream = stream,
            text = if (structuredOutput == null) null else mapOf(
                "format" to structuredOutput
            )
        )
    }
}