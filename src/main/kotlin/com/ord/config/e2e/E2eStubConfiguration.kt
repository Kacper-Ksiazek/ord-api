package com.ord.config.e2e

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

/**
 * Activates fixture-based OpenAI and ElevenLabs stubs when the API runs as a
 * Playwright E2E backend (`SPRING_PROFILES_ACTIVE=e2e`).
 *
 * See README.md — E2E runtime profile.
 */
@Configuration
@Profile("e2e")
@Import(AiStubBeansConfiguration::class)
class E2eStubConfiguration
