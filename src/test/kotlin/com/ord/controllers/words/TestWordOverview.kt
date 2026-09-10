package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.api.capture.requests.dto.PublicCaptureWordItem
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
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
                response.body.pendingCount shouldBe 0
                response.body.unverifiedSourceCount shouldBe 0
                response.body.bookmarkedCount shouldBe 0
            }

            @Test
            fun `200 - should return counts split by lifecycle status`() {
                val user = mockAuthenticatedUser()

                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "pending1", language = LanguageName.POLISH),
                            PublicCaptureWordItem(sourceWord = "pending2", language = LanguageName.POLISH),
                            PublicCaptureWordItem(sourceWord = "pending3", language = LanguageName.POLISH),
                            PublicCaptureWordItem(sourceWord = "pending4", language = LanguageName.POLISH),
                            PublicCaptureWordItem(sourceWord = "pending5", language = LanguageName.POLISH),
                        ),
                    ),
                )

                val response = wordCaptureAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 5
                response.body.activeCount shouldBe 0
                response.body.pendingCount shouldBe 5
                response.body.unverifiedSourceCount shouldBe 5
                response.body.bookmarkedCount shouldBe 0
            }

            @Test
            fun `200 - should only count words belonging to the authenticated user`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()

                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = userA.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "user-a-word", language = LanguageName.POLISH),
                        ),
                    ),
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
                responseA.body.pendingCount shouldBe 1
                responseA.body.unverifiedSourceCount shouldBe 1

                responseB.status shouldBe HttpStatus.OK
                responseB.body!!.total shouldBe 1
                responseB.body.activeCount shouldBe 0
                responseB.body.pendingCount shouldBe 1
                responseB.body.unverifiedSourceCount shouldBe 1
            }

            @Test
            fun `200 - should scope counts to requested language`() {
                val user = mockAuthenticatedUser()

                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "english-pending", language = LanguageName.ENGLISH),
                            PublicCaptureWordItem(sourceWord = "polish-pending", language = LanguageName.POLISH),
                        ),
                    ),
                )

                val englishOverview = wordCaptureAPIClient.getOverview(
                    user = user,
                    language = LanguageName.ENGLISH,
                )
                val polishOverview = wordCaptureAPIClient.getOverview(
                    user = user,
                    language = LanguageName.POLISH,
                )

                englishOverview.status shouldBe HttpStatus.OK
                englishOverview.body!!.total shouldBe 1
                englishOverview.body.pendingCount shouldBe 1

                polishOverview.status shouldBe HttpStatus.OK
                polishOverview.body!!.total shouldBe 1
                polishOverview.body.pendingCount shouldBe 1
            }
        }
    }
}
