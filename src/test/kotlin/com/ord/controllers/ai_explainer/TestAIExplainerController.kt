package com.ord.controllers.ai_explainer

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.testing_utils.api.clients.AIExplainerAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
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
@AutoConfigureWebTestClient(timeout = "180000")
@DisplayName("- AIExplainerController")
class TestAIExplainerController @Autowired constructor(
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository
) {
    private val aiExplainerAPIClient = AIExplainerAPIClient(webClient)

    lateinit var authenticatedUser: MockedAuthenticatedUser

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser(
            languages = mapOf(
                LanguageName.NORWEGIAN to LanguageProficiencyLevel.B2,
                LanguageName.ENGLISH to LanguageProficiencyLevel.C2
            )
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/ai-explainer/explain-phrase - explain word or phrase")
    inner class ExplainPhrase {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should generate explanation for a simple word`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_EXPLAIN_PHRASE")
            }

            @Test
            fun `200 - should generate explanation for a phrase`() {
                val request = ExplainPhraseRequest(
                    phrase = "break the ice",
                    language = LanguageName.ENGLISH
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }

            @Test
            fun `200 - should generate explanation for complex word`() {
                val request = ExplainPhraseRequest(
                    phrase = "hygge",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }

            @Test
            fun `200 - should generate explanation with additional context`() {
                val request = ExplainPhraseRequest(
                    phrase = "break the ice",
                    language = LanguageName.ENGLISH,
                    context = "I was reading a book about networking events. The author mentioned that at parties, people often need to 'break the ice' to start conversations with strangers."
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_EXPLAIN_PHRASE")
            }

            @Test
            fun `200 - should generate explanation with custom instruction in English`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    customInstruction = "Please explain this word as if I'm 5 years old"
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_EXPLAIN_PHRASE")
            }

            @Test
            fun `200 - should generate explanation with custom instruction in another language`() {
                val request = ExplainPhraseRequest(
                    phrase = "katt",
                    language = LanguageName.NORWEGIAN,
                    customInstruction = "Concentrer sur l'usage familier et informel"
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }

            @Test
            fun `200 - should generate explanation with both context and custom instruction`() {
                val request = ExplainPhraseRequest(
                    phrase = "over the moon",
                    language = LanguageName.ENGLISH,
                    context = "She was over the moon when she got the job offer.",
                    customInstruction = "Focus on the emotional aspect and provide similar expressions"
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot get explanation`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - empty phrase should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - extremely long phrase should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "a".repeat(256),
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - user without proficiency in language should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "hola",
                    language = LanguageName.SPANISH
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - context exceeding max length should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    context = "a".repeat(2001)
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - custom instruction exceeding max length should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    customInstruction = "a".repeat(501)
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}