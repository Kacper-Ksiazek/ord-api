package com.ord.core.ai_provider.dto.factories

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.ord.config.properties.OpenAIProperties
import com.ord.core.ai_provider.dto.JsonSchemaDefinition
import com.ord.core.ai_provider.dto.OpenAIRequest
import org.springframework.stereotype.Component

@Component
class OpenAIRequestFactory(
    private val openAIProperties: OpenAIProperties
) {
    private val prettyMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    val defaultContext =
        "Do not include anything more than this JSON and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue."

    fun createRequest(
        // TODO: Migrate to dedicated Prompt class here
        prompt: String,
        context: String = defaultContext,
        stream: Boolean = false,
        structuredOutput: JsonSchemaDefinition? = null,
    ): OpenAIRequest {
        val request = OpenAIRequest(
            model = openAIProperties.gptModel,
            temperature = openAIProperties.temperature,
            max_output_tokens = openAIProperties.maxTokens,
            input = prompt,
            instructions = context,
            stream = stream,
            text = if (structuredOutput == null) null else mapOf(
                "format" to structuredOutput
            )
        )

        // Log structured output request details
        if (structuredOutput != null) {
            println("\n" + "=".repeat(80))
            println("📋 OPENAI STRUCTURED OUTPUT REQUEST")
            println("=".repeat(80))
            println("\n🔹 Schema Name: ${structuredOutput.name}")
            println("🔹 Strict Mode: ${structuredOutput.strict}")
            println("\n📝 Full Request Body:")
            println(prettyMapper.writeValueAsString(request))
            println("\n" + "=".repeat(80) + "\n")
        }

        return request
    }
}