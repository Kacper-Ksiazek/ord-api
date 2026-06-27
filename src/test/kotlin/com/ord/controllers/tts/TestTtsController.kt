package com.ord.controllers.tts

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.features.tts.api.requests.SpeakRequest
import com.ord.testing_utils.api.clients.TtsAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "60000")
@DisplayName("- TtsController")
class TestTtsController @Autowired constructor(
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository,
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository,
) {
    private val ttsAPIClient = TtsAPIClient(webClient)

    lateinit var authenticatedUser: MockedAuthenticatedUser

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser(
            languages = mapOf(
                LanguageName.ENGLISH to LanguageProficiencyLevel.C2,
            )
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/tts/speak - synthesize speech")
    inner class Speak {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should stream audio for valid text`() {
                val request = SpeakRequest(
                    text = "That's a great question! Let me explain..."
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.audioBytes.size shouldBeGreaterThan 0
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot synthesize speech`() {
                val request = SpeakRequest(
                    text = "Hello there"
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = null,
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - empty text should fail`() {
                val request = SpeakRequest(
                    text = ""
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - text exceeding max length should fail`() {
                val request = SpeakRequest(
                    text = "a".repeat(5001)
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}
