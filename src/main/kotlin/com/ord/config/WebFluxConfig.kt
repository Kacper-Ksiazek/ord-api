package com.ord.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.core.auth.annotations.AuthenticatedUserArgumentResolver
import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebFluxConfig(
    private val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver,
    private val objectMapper: ObjectMapper,
    @Value("\${cors.allowed-origins}")
    private val allowedOrigins: String
) : WebFluxConfigurer {

    init {
        // Register JsonNullable module to handle JsonNullable types in requests/responses
        objectMapper.registerModule(JsonNullableModule())
    }

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(authenticatedUserArgumentResolver)
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                *allowedOrigins
                    .split(",")
                    .map { it.trim() }
                    .toTypedArray()
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(7200)
    }
}