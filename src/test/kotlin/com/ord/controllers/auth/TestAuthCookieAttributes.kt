package com.ord.controllers.auth

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.testing_utils.api.clients.AuthAPIClient
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.util.UUID

@DisplayName("- Auth cookie attributes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "3600000")
@TestPropertySource(
    properties = [
        "session.cookie-secure=true",
        "session.cookie-same-site=None",
    ]
)
class TestAuthCookieAttributes @Autowired constructor(
    webClient: WebTestClient,
    sessionProperties: SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository
) : ControllerTestBase(
    webClient,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository
) {
    private val authAPIClient = AuthAPIClient(webClient)

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun productionCookieFlags(registry: DynamicPropertyRegistry) {
            registry.add("session.cookie-secure") { "true" }
            registry.add("session.cookie-same-site") { "None" }
        }
    }

    @Test
    fun `200 - login cookie is Secure and SameSite=None`() {
        val email = "cookie-flags-${UUID.randomUUID()}@example.com"
        try {
            otpCodeRepository.save(
                OtpCodeEntity(
                    code = passwordEncoder.encode("000000")!!,
                    expiresAt = Instant.now().plusSeconds(600),
                    userEmail = email
                )
            ).block()

            val response = authAPIClient.verifyOtp(
                OtpVerifyDto(email = email, code = "000000")
            )

            response.status shouldBe HttpStatus.OK
            val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
            authCookie.shouldNotBeNull()
            authCookie.isSecure shouldBe true
            authCookie.sameSite shouldBe "None"
        } finally {
            userRepository.deleteByEmail(email).block()
            otpCodeRepository.deleteByUserEmail(email).block()
        }
    }

    @Test
    fun `204 - logout cookie matches production flags`() {
        val authenticatedUser = mockAuthenticatedUser()

        val response = authAPIClient.logout(user = authenticatedUser)

        response.status shouldBe HttpStatus.NO_CONTENT
        val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
        authCookie.shouldNotBeNull()
        authCookie.maxAge.seconds shouldBe 0
        authCookie.isSecure shouldBe true
        authCookie.sameSite shouldBe "None"
    }
}
