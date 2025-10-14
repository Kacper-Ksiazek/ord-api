package com.ord.controllers.quickly_added_words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.features.quickly_added_words.repositories.QAWRepository
import com.ord.testing_utils.api.clients.PublicQAWAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
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

@DisplayName("- PublicQuicklyAddedWordsController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestPublicQuicklyAddedWordsController @Autowired constructor(
    private val qawRepository: QAWRepository,
    userRepository: UserRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder
) {
    private val publicQAWAPIClient = PublicQAWAPIClient(webClient)

    object TestData {
        const val TEST_WORD_1 = "example"
        const val TEST_WORD_2 = "word"
        const val TEST_WORD_3 = "test"
        val TEST_LANGUAGE = LanguageName.ENGLISH

        object APIRequestPayloads {
            fun bulkCreate(email: String) = PublicQAWBulkCreateRequest(
                userEmail = email,
                words = listOf(TEST_WORD_1, TEST_WORD_2, TEST_WORD_3),
                language = TEST_LANGUAGE
            )
        }
    }

    @AfterEach
    fun cleanup() {
        qawRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[POST] /api/v1/public/quickly-added-words/bulk-create - create multiple words for a user by email")
    inner class PublicBulkCreateTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var userEmail: String
            lateinit var response: APIClientResponse<List<QuicklyAddedWordEntity>?>

            @BeforeEach
            fun beforeEach() {
                val user = mockAuthenticatedUser()
                userEmail = user.email

                response = publicQAWAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreate(userEmail)
                )
            }

            @Test
            fun `201 - should create multiple quickly added words for user by email`() {
                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!! shouldHaveSize 3
                response.body!!.map { it.word } shouldContain TestData.TEST_WORD_1
                response.body!!.map { it.word } shouldContain TestData.TEST_WORD_2
                response.body!!.map { it.word } shouldContain TestData.TEST_WORD_3
            }

            @Test
            fun `201 - all created words should have correct language`() {
                response.body!!.forEach { word ->
                    word.language shouldBe TestData.TEST_LANGUAGE
                }
            }

            @Test
            fun `201 - all created words should be persisted in database`() {
                val wordsInDb = qawRepository.findAll().collectList().block()!!
                wordsInDb shouldHaveSize 3
            }

            @Test
            fun `201 - all created words should belong to the correct user`() {
                val user = userRepository.findByEmail(userEmail).block()!!

                response.body!!.forEach { word ->
                    word.userId shouldBe user.id
                }
            }

            @Test
            fun `201 - public endpoint should work without authentication`() {
                val newUser = mockAuthenticatedUser()

                val responseWithoutAuth = publicQAWAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreate(newUser.email)
                )

                responseWithoutAuth.status shouldBe HttpStatus.CREATED
                responseWithoutAuth.body shouldNotBe null
            }

            @Test
            fun `201 - words created via public endpoint should NOT be approved by default`() {
                response.body!!.forEach { word ->
                    word.isApproved shouldBe false
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `404 - should return 404 for non-existent user email`() {
                val response = publicQAWAPIClient.publicBulkCreate(
                    PublicQAWBulkCreateRequest(
                        userEmail = "nonexistent@example.com",
                        words = listOf(TestData.TEST_WORD_1),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should return proper error message for non-existent user`() {
                val fakeEmail = faker.internet().emailAddress()

                val response = publicQAWAPIClient.publicBulkCreate(
                    PublicQAWBulkCreateRequest(
                        userEmail = fakeEmail,
                        words = listOf(TestData.TEST_WORD_1),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                response.status shouldBe HttpStatus.NOT_FOUND
                response.body shouldBe null
            }
        }
    }
}