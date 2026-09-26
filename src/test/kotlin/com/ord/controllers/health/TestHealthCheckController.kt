package com.ord.controllers.health

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.health.AiIntegrationMode
import com.ord.core.health.HealthStatus
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.testing_utils.api.clients.HealthCheckAPIClient
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- HealthCheckController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestHealthCheckController @Autowired constructor(
    webClient: WebTestClient,
    sessionProperties: SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository,
) : ControllerTestBase(
    webClient = webClient,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository,
) {
    private val healthCheckAPIClient = HealthCheckAPIClient(webClient)

    @Nested
    @DisplayName("[GET] /api/v1/health-check - application health")
    inner class HealthCheckTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should report component health and the active profile without authentication`() {
                val response = healthCheckAPIClient.healthCheck()

                val health = response.body!!

                response.status shouldBe HttpStatus.OK
                health.application shouldBe HealthStatus.UP
                health.database shouldBe HealthStatus.UP
                health.ai shouldBe AiIntegrationMode.LIVE
                health.tts shouldBe AiIntegrationMode.LIVE
                health.profile shouldBe "test"
            }
        }
    }
}
