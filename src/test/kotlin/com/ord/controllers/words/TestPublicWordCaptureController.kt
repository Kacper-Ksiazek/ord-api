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
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.repositories.WordRepository
import com.ord.testing_utils.api.clients.PublicWordCaptureAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- PublicWordCaptureController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestPublicWordCaptureController @Autowired constructor(
    private val wordRepository: WordRepository,
    userRepository: UserRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository,
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository,
) {
    private val publicWordCaptureAPIClient = PublicWordCaptureAPIClient(webClient)

    object TestData {
        const val TEST_WORD_1 = "example"
        const val TEST_WORD_2 = "word"
        const val TEST_WORD_3 = "test"
        const val TEST_TRANSLATION = "ejemplo"
        const val TEST_DEFINITION = "A sample word for testing"
        val TEST_LANGUAGE = LanguageName.ENGLISH
        val TEST_EXTRA_MARK = WordExtraMark.SLANG
        val TEST_TYPE = WordType.NOUN

        object APIRequestPayloads {
            fun bulkCreate(email: String) = PublicWordsBulkCaptureRequest(
                userEmail = email,
                words = listOf(
                    PublicCaptureWordItem(sourceWord = TEST_WORD_1, language = TEST_LANGUAGE),
                    PublicCaptureWordItem(sourceWord = TEST_WORD_2, language = TEST_LANGUAGE),
                    PublicCaptureWordItem(sourceWord = TEST_WORD_3, language = TEST_LANGUAGE),
                ),
            )

            fun bulkCreateWithAllFields(email: String) = PublicWordsBulkCaptureRequest(
                userEmail = email,
                words = listOf(
                    PublicCaptureWordItem(
                        sourceWord = TEST_WORD_1,
                        language = TEST_LANGUAGE,
                        translation = TEST_TRANSLATION,
                        definition = TEST_DEFINITION,
                        extraMark = TEST_EXTRA_MARK,
                        type = TEST_TYPE,
                    ),
                    PublicCaptureWordItem(
                        sourceWord = TEST_WORD_2,
                        language = TEST_LANGUAGE,
                        translation = "palabra",
                        definition = "Another definition",
                        extraMark = WordExtraMark.OFFENSIVE,
                        type = WordType.VERB,
                    ),
                ),
            )
        }
    }

    private fun wordsForUser(email: String) =
        userRepository.findByEmail(email).block()!!.let { user ->
            wordRepository.findAll().collectList().block()!!.filter { it.userId == user.id }
        }

    @Nested
    @DisplayName("[POST] /api/v1/public/words/bulk-create - create multiple words for a user by email")
    inner class PublicBulkCreateTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var userEmail: String
            lateinit var response: APIClientResponse<Void?>

            @BeforeEach
            fun beforeEach() {
                val user = mockAuthenticatedUser()
                userEmail = user.email

                response = publicWordCaptureAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreate(userEmail),
                )
            }

            @Test
            fun `204 - should create multiple captured words for user by email`() {
                response.status shouldBe HttpStatus.NO_CONTENT
                response.body shouldBe null
            }

            @Test
            fun `204 - all created words should be persisted in database`() {
                val wordsInDb = wordsForUser(userEmail)
                wordsInDb shouldHaveSize 3
                wordsInDb.map { it.sourceWord } shouldContain TestData.TEST_WORD_1
                wordsInDb.map { it.sourceWord } shouldContain TestData.TEST_WORD_2
                wordsInDb.map { it.sourceWord } shouldContain TestData.TEST_WORD_3
            }

            @Test
            fun `204 - all created words should have correct language`() {
                val wordsInDb = wordsForUser(userEmail)
                wordsInDb.forEach { word ->
                    word.language shouldBe TestData.TEST_LANGUAGE
                }
            }

            @Test
            fun `204 - all created words should belong to the correct user`() {
                val user = userRepository.findByEmail(userEmail).block()!!
                val wordsInDb = wordsForUser(userEmail)

                wordsInDb.forEach { word ->
                    word.userId shouldBe user.id
                }
            }

            @Test
            fun `204 - public endpoint should work without authentication`() {
                val newUser = mockAuthenticatedUser()

                val responseWithoutAuth = publicWordCaptureAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreate(newUser.email),
                )

                responseWithoutAuth.status shouldBe HttpStatus.NO_CONTENT
                responseWithoutAuth.body shouldBe null
            }

            @Test
            fun `204 - words created via public endpoint should be CAPTURED by default`() {
                val wordsInDb = wordsForUser(userEmail)
                wordsInDb.forEach { word ->
                    word.status shouldBe WordStatus.CAPTURED
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `404 - should return 404 for non-existent user email`() {
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = "nonexistent@example.com",
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = TestData.TEST_WORD_1, language = TestData.TEST_LANGUAGE),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should return proper error message for non-existent user`() {
                val fakeEmail = faker.internet().emailAddress()

                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = fakeEmail,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = TestData.TEST_WORD_1, language = TestData.TEST_LANGUAGE),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.NOT_FOUND
                response.body shouldBe null
            }

            @Test
            fun `400 - should reject invalid email format`() {
                mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = "invalid-email",
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = TestData.TEST_WORD_1, language = TestData.TEST_LANGUAGE),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject empty words list`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject empty word in word item`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(PublicCaptureWordItem(sourceWord = "", language = TestData.TEST_LANGUAGE)),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject blank word in word item`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(PublicCaptureWordItem(sourceWord = "   ", language = TestData.TEST_LANGUAGE)),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject word exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "a".repeat(256), language = TestData.TEST_LANGUAGE),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject definition exceeding 2000 characters`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(
                                sourceWord = "test",
                                language = TestData.TEST_LANGUAGE,
                                definition = "a".repeat(2001),
                            ),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject translation exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(
                                sourceWord = "test",
                                language = TestData.TEST_LANGUAGE,
                                translation = "a".repeat(256),
                            ),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject words list exceeding 100 items`() {
                val user = mockAuthenticatedUser()
                val tooManyWords = (1..101).map {
                    PublicCaptureWordItem(sourceWord = "word$it", language = TestData.TEST_LANGUAGE)
                }

                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = tooManyWords,
                    ),
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/public/words/bulk-create - create words with all fields")
    inner class PublicBulkCreateWithAllFieldsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `204 - should create words with translation, definition, extraMark, and type`() {
                val user = mockAuthenticatedUser()
                val response = publicWordCaptureAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreateWithAllFields(user.email),
                )

                response.status shouldBe HttpStatus.NO_CONTENT
                response.body shouldBe null

                val wordsInDb = wordsForUser(user.email)
                wordsInDb shouldHaveSize 2

                val firstWord = wordsInDb.find { it.sourceWord == TestData.TEST_WORD_1 }!!
                firstWord.sourceWord shouldBe TestData.TEST_WORD_1
                firstWord.translation shouldBe TestData.TEST_TRANSLATION
                firstWord.definition shouldBe TestData.TEST_DEFINITION
                firstWord.extraMark shouldBe TestData.TEST_EXTRA_MARK
                firstWord.type shouldBe TestData.TEST_TYPE

                val secondWord = wordsInDb.find { it.sourceWord == TestData.TEST_WORD_2 }!!
                secondWord.sourceWord shouldBe TestData.TEST_WORD_2
                secondWord.translation shouldBe "palabra"
                secondWord.definition shouldBe "Another definition"
                secondWord.extraMark shouldBe WordExtraMark.OFFENSIVE
                secondWord.type shouldBe WordType.VERB
            }

            @Test
            fun `204 - words with all fields should still be CAPTURED`() {
                val user = mockAuthenticatedUser()
                publicWordCaptureAPIClient.publicBulkCreate(
                    TestData.APIRequestPayloads.bulkCreateWithAllFields(user.email),
                )

                val wordsInDb = wordsForUser(user.email)
                wordsInDb.forEach { word ->
                    word.status shouldBe WordStatus.CAPTURED
                }
            }
        }
    }
}
