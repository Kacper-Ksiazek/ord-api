package com.ord.controllers.quickly_added_words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem
import com.ord.features.quickly_added_words.repositories.QAWRepository
import com.ord.testing_utils.api.clients.PublicQAWAPIClient
import com.ord.testing_utils.api.clients.QAWAPIClient
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- QuicklyAddedWordsController: Overview")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestQAWOverview @Autowired constructor(
    private val qawRepository: QAWRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
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
    private val qawAPIClient = QAWAPIClient(webClient)
    private val publicQAWAPIClient = PublicQAWAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        qawRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[GET] /api/v1/quickly-added-words/overview")
    inner class GetOverview {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = qawAPIClient.getOverview(user = null)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return zero counts for user with no words`() {
                val user = mockAuthenticatedUser()

                val response = qawAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 0
                response.body.approvedCount shouldBe 0
                response.body.unapprovedCount shouldBe 0
            }

            @Test
            fun `200 - should return counts split by approval status`() {
                val user = mockAuthenticatedUser()

                qawAPIClient.bulkCreate(
                    listOf(
                        CreateQAWRequest(word = "approved1", language = LanguageName.POLISH),
                        CreateQAWRequest(word = "approved2", language = LanguageName.POLISH),
                    ),
                    user
                )
                publicQAWAPIClient.publicBulkCreate(
                    PublicQAWBulkCreateRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicQAWWordItem(word = "pending1"),
                            PublicQAWWordItem(word = "pending2"),
                            PublicQAWWordItem(word = "pending3"),
                        ),
                        language = LanguageName.POLISH
                    )
                )

                val response = qawAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 5
                response.body.approvedCount shouldBe 2
                response.body.unapprovedCount shouldBe 3
            }

            @Test
            fun `200 - should only count words belonging to the authenticated user`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()

                qawAPIClient.createOne(
                    CreateQAWRequest(word = "user-a-word", language = LanguageName.POLISH),
                    userA
                )
                publicQAWAPIClient.publicBulkCreate(
                    PublicQAWBulkCreateRequest(
                        userEmail = userB.email,
                        words = listOf(PublicQAWWordItem(word = "user-b-pending")),
                        language = LanguageName.POLISH
                    )
                )

                val responseA = qawAPIClient.getOverview(user = userA)
                val responseB = qawAPIClient.getOverview(user = userB)

                responseA.status shouldBe HttpStatus.OK
                responseA.body!!.total shouldBe 1
                responseA.body.approvedCount shouldBe 1
                responseA.body.unapprovedCount shouldBe 0

                responseB.status shouldBe HttpStatus.OK
                responseB.body!!.total shouldBe 1
                responseB.body.approvedCount shouldBe 0
                responseB.body.unapprovedCount shouldBe 1
            }
        }
    }
}
