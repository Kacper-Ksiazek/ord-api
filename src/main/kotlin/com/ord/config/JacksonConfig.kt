package com.ord.config

import com.ord.shared.utils.OrdJsonMapper
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun kotlinJacksonCustomizer(): JsonMapperBuilderCustomizer =
        JsonMapperBuilderCustomizer { builder ->
            OrdJsonMapper.configureHttpKotlinModule(builder)
        }
}
