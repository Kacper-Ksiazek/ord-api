package com.ord.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.core.auth.annotations.AuthenticatedUserArgumentResolver
import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebFluxConfig(
    private val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver,
    private val objectMapper: ObjectMapper
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
}