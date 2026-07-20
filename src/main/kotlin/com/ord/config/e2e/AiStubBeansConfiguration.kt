package com.ord.config.e2e

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.core.tts.services.ElevenLabsTTSClientService
import com.ord.stubs.ai.AIFixtureDynamicBuilder
import com.ord.stubs.ai.AIFixtureLoader
import com.ord.stubs.ai.AIFixtureRegistry
import com.ord.stubs.ai.StubElevenLabsTTSClientService
import com.ord.stubs.ai.StubOpenAIAPIClientService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Shared bean definitions for AI/TTS stubs.
 * Imported by the `e2e` runtime profile and by smoke-test `@TestConfiguration`.
 */
@Configuration
class AiStubBeansConfiguration {

    @Bean
    fun aiFixtureRegistry(): AIFixtureRegistry = AIFixtureRegistry()

    @Bean
    fun aiFixtureLoader(fixtureRegistry: AIFixtureRegistry): AIFixtureLoader =
        AIFixtureLoader(fixtureRegistry)

    @Bean
    fun aiFixtureDynamicBuilder(fixtureLoader: AIFixtureLoader): AIFixtureDynamicBuilder =
        AIFixtureDynamicBuilder(fixtureLoader)

    @Bean(name = ["openAIAPIClientServiceImpl"])
    @Primary
    fun openAIAPIClientServiceImpl(
        gptTokensUsageService: GptTokensUsageService,
        fixtureLoader: AIFixtureLoader,
        dynamicBuilder: AIFixtureDynamicBuilder,
    ): OpenAIAPIClientService = StubOpenAIAPIClientService(
        gptTokensUsageService = gptTokensUsageService,
        fixtureLoader = fixtureLoader,
        dynamicBuilder = dynamicBuilder,
    )

    @Bean
    @Primary
    fun elevenLabsTTSClientService(): ElevenLabsTTSClientService =
        StubElevenLabsTTSClientService()
}
