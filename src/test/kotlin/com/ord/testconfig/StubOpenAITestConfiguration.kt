package com.ord.testconfig

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.testconfig.stub.StubOpenAIAPIClientService
import com.ord.testing_utils.mocks.ai.AIFixtureDynamicBuilder
import com.ord.testing_utils.mocks.ai.AIFixtureLoader
import com.ord.testing_utils.mocks.ai.AIFixtureRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
@ConditionalOnExpression("'\${INTEGRATION_TESTS:}' != 'true'")
class StubOpenAITestConfiguration {

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
}
