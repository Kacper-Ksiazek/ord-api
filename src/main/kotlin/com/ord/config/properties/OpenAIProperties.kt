package com.ord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@Configuration
@ConfigurationProperties(prefix = "openai")
class OpenAIProperties(
    var apiKey: String = "",
    var apiUrl: String = "",
    var gptModel: String = "",
    var maxTokens: Int = 0,
    var temperature: Float = 0.7f,

    var pricePerMlnInputTokens: BigDecimal = BigDecimal.ZERO,
    var pricePerMlnOutputTokens: BigDecimal = BigDecimal.ZERO,

    var maximumNumberOfOpenAIAPIRequestAttempts: Int = 5
) {
    val authenticationHeaderValue: String
        get() = "Bearer $apiKey"
}
