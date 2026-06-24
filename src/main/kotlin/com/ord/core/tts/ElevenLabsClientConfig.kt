package com.ord.core.tts

import com.ord.config.properties.ElevenLabsProperties
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
class ElevenLabsClientConfig(
    private val elevenLabsProperties: ElevenLabsProperties,
) {
    @Bean("elevenLabsWebClient")
    fun elevenLabsWebClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elevenLabsProperties.connectTimeoutSeconds * 1000)
            .responseTimeout(Duration.ofSeconds(elevenLabsProperties.readTimeoutSeconds.toLong()))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(elevenLabsProperties.readTimeoutSeconds))
                    .addHandlerLast(WriteTimeoutHandler(elevenLabsProperties.writeTimeoutSeconds))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(elevenLabsProperties.apiUrl)
            .defaultHeader("xi-api-key", elevenLabsProperties.apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}
