package com.ord.controllers.auth

import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- OtpCopyPageController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "3600000")
class TestOtpCopyPageController @Autowired constructor(
    webClient: WebTestClient,
    sessionProperties: com.ord.config.properties.SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository,
) : ControllerTestBase(
    webClient,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository,
) {
    @Nested
    @DisplayName("[GET] /public/otp-copy")
    inner class GetCopyPage {
        @Test
        fun `200 - should return HTML copy page for a valid code`() {
            webClient.get()
                .uri("/public/otp-copy?code=123456")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .value { body ->
                    body shouldContain "123456"
                    body shouldContain "navigator.clipboard.writeText"
                    body shouldContain "Continue to sign in"
                }
        }

        @Test
        fun `400 - should reject invalid code`() {
            webClient.get()
                .uri("/public/otp-copy?code=abc")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `200 - should not echo script injection from code`() {
            webClient.get()
                .uri("/public/otp-copy?code=%3Cscript%3E")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isBadRequest
        }
    }
}
