package com.ord.testconfig

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webtestclient.autoconfigure.WebTestClientBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

@TestConfiguration
class WebTestClientOriginHeaderConfiguration {
    @Bean
    fun webTestClientOriginHeaderCustomizer(
        @Value("\${cors.allowed-origins}") allowedOrigins: String,
    ): WebTestClientBuilderCustomizer {
        val origin = allowedOrigins.split(",").first().trim()
        return WebTestClientBuilderCustomizer { builder ->
            builder.defaultHeader(HttpHeaders.ORIGIN, origin)
        }
    }
}
