package com.ord.testconfig

import com.ord.config.e2e.AiStubBeansConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@TestConfiguration
@ConditionalOnExpression("'\${INTEGRATION_TESTS:}' != 'true'")
@Import(AiStubBeansConfiguration::class)
class StubOpenAITestConfiguration
