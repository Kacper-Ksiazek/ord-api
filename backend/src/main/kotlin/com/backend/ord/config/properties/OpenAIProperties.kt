package com.backend.ord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "openai")
open class OpenAIProperties(
    var apiKey: String = "",
    var apiUrl: String = "",
    var gptModel: String = "",
    var maxTokens: Int = 0,
    var temperature: Float = 0f
) {
    val authenticationHeaderValue: String
        get() = "Bearer $apiKey"
}
