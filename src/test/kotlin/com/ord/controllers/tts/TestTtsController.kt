package com.ord.controllers.tts

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.ai_provider_usage.models.AiProviderUsageOperationType
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
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
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "60000")
@DisplayName("- TtsController")
class TestTtsController @Autowired constructor(
    sessionProperties: SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
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
            fun `200 - should stream audio when language is provided explicitly`() {
                val request = SpeakRequest(
                    text = "That's a great question! Let me explain...",
                    language = LanguageName.ENGLISH,
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.audioBytes.size shouldBeGreaterThan 0
                assertAiProviderUsageLogCreated(
                    authenticatedUser.userInfo.id,
                    AiProviderUsageOperationType.Tts.SPEAK,
                )
            }

            @Test
            fun `200 - should stream audio using selectedLearningLanguage as fallback`() {
                val request = SpeakRequest(
                    text = "That's a great question! Let me explain...",
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.audioBytes.size shouldBeGreaterThan 0
            }

            @Test
            fun `200 - should stream audio for supported German voice`() {
                val request = SpeakRequest(
                    text = "Das ist eine gute Frage!",
                    language = LanguageName.GERMAN,
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
                    text = "Hello there",
                    language = LanguageName.ENGLISH,
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
                    text = "",
                    language = LanguageName.ENGLISH,
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
                    text = "a".repeat(5001),
                    language = LanguageName.ENGLISH,
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - unsupported language should fail`() {
                val request = SpeakRequest(
                    text = "Bonjour",
                    language = LanguageName.FRENCH,
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - missing language when user has no selectedLearningLanguage should fail`() {
                val userWithoutLearningLanguage = mockAuthenticatedUserWithUninitializedAccount()
                val request = SpeakRequest(
                    text = "Hello there",
                )

                val response = ttsAPIClient.speak(
                    body = request,
                    user = userWithoutLearningLanguage,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}
