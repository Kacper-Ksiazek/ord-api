package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.PublicCaptureWordItem
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.repositories.WordRepository
import com.ord.testing_utils.api.clients.PublicWordCaptureAPIClient
import com.ord.testing_utils.api.clients.WordCaptureAPIClient
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- WordCaptureController: Overview")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestWordOverview @Autowired constructor(
    private val wordRepository: WordRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
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
    private val wordCaptureAPIClient = WordCaptureAPIClient(webClient)
    private val publicWordCaptureAPIClient = PublicWordCaptureAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        wordRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[GET] /api/v1/words/overview")
    inner class GetOverview {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = wordCaptureAPIClient.getOverview(user = null)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return zero counts for user with no words`() {
                val user = mockAuthenticatedUser()

                val response = wordCaptureAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 0
                response.body.activeCount shouldBe 0
                response.body.capturedCount shouldBe 0
            }

            @Test
            fun `200 - should return counts split by lifecycle status`() {
                val user = mockAuthenticatedUser()

                wordCaptureAPIClient.capture(
                    listOf(
                        CaptureWordRequest(sourceWord = "captured1", language = LanguageName.POLISH),
                        CaptureWordRequest(sourceWord = "captured2", language = LanguageName.POLISH),
                    ),
                    user,
                )
                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "pending1", language = LanguageName.POLISH),
                            PublicCaptureWordItem(sourceWord = "pending2", language = LanguageName.POLISH),
                            PublicCaptureWordItem(sourceWord = "pending3", language = LanguageName.POLISH),
                        ),
                    ),
                )

                val response = wordCaptureAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 5
                response.body.activeCount shouldBe 0
                response.body.capturedCount shouldBe 5
            }

            @Test
            fun `200 - should only count words belonging to the authenticated user`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()

                wordCaptureAPIClient.captureOne(
                    CaptureWordRequest(sourceWord = "user-a-word", language = LanguageName.POLISH),
                    userA,
                )
                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = userB.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "user-b-pending", language = LanguageName.POLISH),
                        ),
                    ),
                )

                val responseA = wordCaptureAPIClient.getOverview(user = userA)
                val responseB = wordCaptureAPIClient.getOverview(user = userB)

                responseA.status shouldBe HttpStatus.OK
                responseA.body!!.total shouldBe 1
                responseA.body.activeCount shouldBe 0
                responseA.body.capturedCount shouldBe 1

                responseB.status shouldBe HttpStatus.OK
                responseB.body!!.total shouldBe 1
                responseB.body.activeCount shouldBe 0
                responseB.body.capturedCount shouldBe 1
            }
        }
    }
}
