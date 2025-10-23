package com.ord.controllers.quickly_added_words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem
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
        const val TEST_DEFINITION = "A sample word for testing"
        val TEST_LANGUAGE = LanguageName.ENGLISH
        val TEST_EXTRA_MARK = WordExtraMark.SLANG
        val TEST_TYPE = WordType.NOUN

        object APIRequestPayloads {
            fun bulkCreate(email: String) = PublicQAWBulkCreateRequest(
                userEmail = email,
                words = listOf(
                    PublicQAWWordItem(word = TEST_WORD_1),
                    PublicQAWWordItem(word = TEST_WORD_2),
                    PublicQAWWordItem(word = TEST_WORD_3)
                ),
                language = TEST_LANGUAGE
            )

            fun bulkCreateWithAllFields(email: String) = PublicQAWBulkCreateRequest(
                userEmail = email,
                words = listOf(
                    PublicQAWWordItem(
                        word = TEST_WORD_1,
                        definition = TEST_DEFINITION,
                        extraMark = TEST_EXTRA_MARK,
                        type = TEST_TYPE
                    ),
                    PublicQAWWordItem(
                        word = TEST_WORD_2,
                        definition = "Another definition",
                        extraMark = WordExtraMark.OFFENSIVE,
                        type = WordType.VERB
                    )
                ),
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
                        words = listOf(PublicQAWWordItem(word = TestData.TEST_WORD_1)),
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
                        words = listOf(PublicQAWWordItem(word = TestData.TEST_WORD_1)),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                response.status shouldBe HttpStatus.NOT_FOUND
                response.body shouldBe null
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/public/quickly-added-words/bulk-create - create words with all fields")
    inner class PublicBulkCreateWithAllFieldsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create words with definition, extraMark, and type`() {
                val user = mockAuthenticatedUser()
                val response = publicQAWAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreateWithAllFields(user.email)
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!! shouldHaveSize 2

                val firstWord = response.body!![0]
                firstWord.word shouldBe TestData.TEST_WORD_1
                firstWord.definition shouldBe TestData.TEST_DEFINITION
                firstWord.extraMark shouldBe TestData.TEST_EXTRA_MARK
                firstWord.type shouldBe TestData.TEST_TYPE

                val secondWord = response.body!![1]
                secondWord.word shouldBe TestData.TEST_WORD_2
                secondWord.definition shouldBe "Another definition"
                secondWord.extraMark shouldBe WordExtraMark.OFFENSIVE
                secondWord.type shouldBe WordType.VERB
            }

            @Test
            fun `201 - words with all fields should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val response = publicQAWAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreateWithAllFields(user.email)
                )

                val wordInDb = qawRepository.findById(response.body!![0].id!!).block()
                wordInDb shouldNotBe null
                wordInDb!!.definition shouldBe TestData.TEST_DEFINITION
                wordInDb.extraMark shouldBe TestData.TEST_EXTRA_MARK
                wordInDb.type shouldBe TestData.TEST_TYPE
            }

            @Test
            fun `201 - should support mixed words (some with fields, some without)`() {
                val user = mockAuthenticatedUser()
                val response = publicQAWAPIClient.publicBulkCreate(
                    PublicQAWBulkCreateRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicQAWWordItem(
                                word = TestData.TEST_WORD_1,
                                definition = TestData.TEST_DEFINITION,
                                extraMark = TestData.TEST_EXTRA_MARK,
                                type = TestData.TEST_TYPE
                            ),
                            PublicQAWWordItem(word = TestData.TEST_WORD_2),  // Only word, no extras
                            PublicQAWWordItem(
                                word = TestData.TEST_WORD_3,
                                definition = "Only definition"  // Only word and definition
                            )
                        ),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                response.status shouldBe HttpStatus.CREATED
                response.body!! shouldHaveSize 3

                // First word has all fields
                response.body!![0].definition shouldBe TestData.TEST_DEFINITION
                response.body!![0].extraMark shouldBe TestData.TEST_EXTRA_MARK
                response.body!![0].type shouldBe TestData.TEST_TYPE

                // Second word has no extras
                response.body!![1].definition shouldBe null
                response.body!![1].extraMark shouldBe null
                response.body!![1].type shouldBe null

                // Third word has only definition
                response.body!![2].definition shouldBe "Only definition"
                response.body!![2].extraMark shouldBe null
                response.body!![2].type shouldBe null
            }

            @Test
            fun `201 - words with all fields should still be unapproved`() {
                val user = mockAuthenticatedUser()
                val response = publicQAWAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreateWithAllFields(user.email)
                )

                response.body!!.forEach { word ->
                    word.isApproved shouldBe false
                }
            }
        }
    }
}