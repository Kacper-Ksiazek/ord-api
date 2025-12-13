package com.ord.core.ai_provider

import com.ord.config.properties.OpenAIProperties
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class OpenAIClientConfig(
    private val openAIProperties: OpenAIProperties
) {
    @Bean
    fun webClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openAIProperties.connectTimeoutSeconds * 1000)
            .responseTimeout(Duration.ofSeconds(openAIProperties.readTimeoutSeconds.toLong()))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(openAIProperties.readTimeoutSeconds))
                    .addHandlerLast(WriteTimeoutHandler(openAIProperties.readTimeoutSeconds))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(openAIProperties.apiUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, openAIProperties.authenticationHeaderValue)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}