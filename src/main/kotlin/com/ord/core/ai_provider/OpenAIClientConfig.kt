package com.ord.core.ai_provider

import com.ord.config.properties.OpenAIProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class OpenAIClientConfig(
    private val openAIProperties: OpenAIProperties
) {
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl(openAIProperties.apiUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, openAIProperties.authenticationHeaderValue)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}