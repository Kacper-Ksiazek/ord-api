package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.api.capture.requests.dto.PublicCaptureWordItem
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.testing_utils.api.clients.PublicWordCaptureAPIClient
import com.ord.testing_utils.api.clients.WordsAPIClient
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.repositories.WordRepository
import com.ord.testing_utils.api.clients.WordCaptureAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.Stream
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@DisplayName("- WordCaptureController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestWordCaptureController @Autowired constructor(
    private val wordRepository: WordRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    _userRepository: UserRepository,
    _otpCodeRepository: OtpCodeRepository,
    _passwordEncoder: PasswordEncoder,
    _gptTokensUsageRepository: GptTokensUsageRepository
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = _userRepository,
    otpCodeRepository = _otpCodeRepository,
    passwordEncoder = _passwordEncoder,
    gptTokensUsageRepository = _gptTokensUsageRepository
) {
    private val wordCaptureAPIClient = WordCaptureAPIClient(webClient)
    private val wordsAPIClient = WordsAPIClient(webClient)

    object TestData {
        const val TEST_WORD_1 = "przykład"
        const val TEST_WORD_2 = "słowo"
        const val TEST_WORD_3 = "test"
        const val TEST_TRANSLATION = "example"
        const val TEST_DEFINITION = "A sample word for testing"
        val TEST_LANGUAGE = LanguageName.POLISH
        val TEST_EXTRA_MARK = WordExtraMark.SLANG
        val TEST_TYPE = WordType.NOUN

        fun activationReadyPublicItem(sourceWord: String) = PublicCaptureWordItem(
            sourceWord = sourceWord,
            language = TEST_LANGUAGE,
            translation = TEST_TRANSLATION,
            definition = TEST_DEFINITION,
            type = TEST_TYPE,
        )

        object APIRequestPayloads {
            val createOne = CaptureWordRequest(sourceWord = TEST_WORD_1,
                language = TEST_LANGUAGE
            )

            val createOneWithAllFields = CaptureWordRequest(sourceWord = TEST_WORD_1,
                language = TEST_LANGUAGE,
                translation = TEST_TRANSLATION,
                definition = TEST_DEFINITION,
                extraMark = TEST_EXTRA_MARK,
                type = TEST_TYPE
            )

            val createBulk = listOf(
                CaptureWordRequest(sourceWord = TEST_WORD_1, language = TEST_LANGUAGE),
                CaptureWordRequest(sourceWord = TEST_WORD_2, language = TEST_LANGUAGE),
                CaptureWordRequest(sourceWord = TEST_WORD_3, language = TEST_LANGUAGE)
            )

            val updateOne = UpdateCapturedWordRequest(
                sourceWord = "zaktualizowane"
            )

            val updateOneWithAllFields = UpdateCapturedWordRequest(
                sourceWord = "zaktualizowane",
                translation = "updated",
                definition = "Updated definition",
                extraMark = WordExtraMark.OFFENSIVE,
                type = WordType.VERB
            )
        }
    }

    @AfterEach
    fun cleanup() {
        wordRepository.deleteAll().block()  // progress rows cascade via FK or separate cleanup in test base
    }

    companion object {
        @JvmStatic
        fun statusFilterCases(): Stream<Arguments> = Stream.of(
            Arguments.of(WordStatus.ACTIVE, 1, null as Long?, WordStatus.ACTIVE),
            Arguments.of(WordStatus.CAPTURED, 1, null as Long?, WordStatus.CAPTURED),
            Arguments.of(null as WordStatus?, 2, 1L, null as WordStatus?),
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/capture - create one quickly added word")
    inner class CreateOneTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var response: APIClientResponse<WordDTO?>

            @BeforeEach
            fun beforeEach() {
                val user = mockAuthenticatedUser()
                response = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)
            }

            @Test
            fun `201 - should create a quickly added word`() {
                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.sourceWord shouldBe TestData.TEST_WORD_1
                response.body!!.language shouldBe TestData.TEST_LANGUAGE
                response.body!!.id shouldNotBe null
            }

            @Test
            fun `201 - created word should be persisted in database`() {
                val wordInDb = wordRepository.findById(response.body!!.id).block()
                wordInDb shouldNotBe null
                wordInDb!!.sourceWord shouldBe TestData.TEST_WORD_1
            }

            @Test
            fun `201 - created word should be approved by default`() {
                response.body!!.status shouldBe WordStatus.CAPTURED
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should reject empty word`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = "", language = TestData.TEST_LANGUAGE)

                val response = wordCaptureAPIClient.captureOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject blank word`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = "   ", language = TestData.TEST_LANGUAGE)

                val response = wordCaptureAPIClient.captureOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject word exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = "a".repeat(256), language = TestData.TEST_LANGUAGE)

                val response = wordCaptureAPIClient.captureOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject definition exceeding 2000 characters`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = "test",
                    language = TestData.TEST_LANGUAGE,
                    definition = "a".repeat(2001)
                )

                val response = wordCaptureAPIClient.captureOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject translation exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = "test",
                    language = TestData.TEST_LANGUAGE,
                    translation = "a".repeat(256)
                )

                val response = wordCaptureAPIClient.captureOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/capture - create one quickly added word with translation")
    inner class CreateOneWithTranslationTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create a word with only translation (no definition)`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = TestData.TEST_WORD_1,
                    language = TestData.TEST_LANGUAGE,
                    translation = TestData.TEST_TRANSLATION
                )

                val response = wordCaptureAPIClient.captureOne(request, user)

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.sourceWord shouldBe TestData.TEST_WORD_1
                response.body!!.translation shouldBe TestData.TEST_TRANSLATION
                response.body!!.definition shouldBe null
            }

            @Test
            fun `201 - word with translation should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val request = CaptureWordRequest(sourceWord = TestData.TEST_WORD_1,
                    language = TestData.TEST_LANGUAGE,
                    translation = TestData.TEST_TRANSLATION
                )

                val response = wordCaptureAPIClient.captureOne(request, user)

                val wordInDb = wordRepository.findById(response.body!!.id).block()
                wordInDb shouldNotBe null
                wordInDb!!.translation shouldBe TestData.TEST_TRANSLATION
                wordInDb.definition shouldBe null
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/capture - create one quickly added word with all fields")
    inner class CreateOneWithAllFieldsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create a quickly added word with translation, definition, extraMark, and type`() {
                val user = mockAuthenticatedUser()
                val response = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.sourceWord shouldBe TestData.TEST_WORD_1
                response.body!!.language shouldBe TestData.TEST_LANGUAGE
                response.body!!.translation shouldBe TestData.TEST_TRANSLATION
                response.body!!.definition shouldBe TestData.TEST_DEFINITION
                response.body!!.extraMark shouldBe TestData.TEST_EXTRA_MARK
                response.body!!.type shouldBe TestData.TEST_TYPE
            }

            @Test
            fun `201 - created word with all fields should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val response = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val wordInDb = wordRepository.findById(response.body!!.id).block()
                wordInDb shouldNotBe null
                wordInDb!!.translation shouldBe TestData.TEST_TRANSLATION
                wordInDb.definition shouldBe TestData.TEST_DEFINITION
                wordInDb.extraMark shouldBe TestData.TEST_EXTRA_MARK
                wordInDb.type shouldBe TestData.TEST_TYPE
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/bulk-capture - create multiple quickly added words")
    inner class BulkCreateTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var response: APIClientResponse<List<WordDTO>?>

            @BeforeEach
            fun beforeEach() {
                val user = mockAuthenticatedUser()
                response = wordCaptureAPIClient.bulkCapture(TestData.APIRequestPayloads.createBulk, user)
            }

            @Test
            fun `201 - should create multiple quickly added words`() {
                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!! shouldHaveSize 3
                response.body!!.map { it.sourceWord } shouldContain TestData.TEST_WORD_1
                response.body!!.map { it.sourceWord } shouldContain TestData.TEST_WORD_2
                response.body!!.map { it.sourceWord } shouldContain TestData.TEST_WORD_3
            }

            @Test
            fun `201 - all created words should be persisted in database`() {
                val wordsInDb = wordRepository.findAll().collectList().block()!!
                wordsInDb shouldHaveSize 3
            }

            @Test
            fun `201 - all created words should be captured by default`() {
                response.body!!.forEach { word ->
                    word.status shouldBe WordStatus.CAPTURED
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.bulkCapture(TestData.APIRequestPayloads.createBulk)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/words/captured - list captured words quickly added words")
    inner class GetCapturedWordsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return paginated list of quickly added words`() {
                val user = mockAuthenticatedUser()
                wordCaptureAPIClient.bulkCapture(TestData.APIRequestPayloads.createBulk, user)

                val response = wordCaptureAPIClient.getCapturedWords(user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 3
                response.body.pagination.totalResults shouldBe 3
            }

            @Test
            fun `200 - should respect pagination parameters`() {
                val user = mockAuthenticatedUser()
                wordCaptureAPIClient.bulkCapture(TestData.APIRequestPayloads.createBulk, user)

                val response = wordCaptureAPIClient.getCapturedWords(page = 0, perPage = 2, user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 2
                response.body.pagination.totalResults shouldBe 3
                response.body.pagination.page shouldBe 0
                response.body.pagination.perPage shouldBe 2
            }

            @Test
            fun `200 - page 1 should return second page when enough items exist`() {
                val user = mockAuthenticatedUser()
                val words = (1..51).map { CaptureWordRequest(sourceWord = "word-$it", language = TestData.TEST_LANGUAGE) }
                wordCaptureAPIClient.bulkCapture(words, user)

                val firstPage = wordCaptureAPIClient.getCapturedWords(page = 0, perPage = 50, user = user)
                val secondPage = wordCaptureAPIClient.getCapturedWords(page = 1, perPage = 50, user = user)

                firstPage.status shouldBe HttpStatus.OK
                firstPage.body!!.data shouldHaveSize 50
                firstPage.body.pagination.page shouldBe 0
                firstPage.body.pagination.totalResults shouldBe 51

                secondPage.status shouldBe HttpStatus.OK
                secondPage.body!!.data shouldHaveSize 1
                secondPage.body.pagination.page shouldBe 1
                secondPage.body.pagination.totalResults shouldBe 51
            }

            @Test
            fun `200 - should only return words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user1)
                wordCaptureAPIClient.captureOne(
                    CaptureWordRequest(sourceWord = "other", language = TestData.TEST_LANGUAGE),
                    user2
                )

                val response = wordCaptureAPIClient.getCapturedWords(user = user1)

                response.body!!.data shouldHaveSize 1
                response.body.data[0].sourceWord shouldBe TestData.TEST_WORD_1
            }

            @Test
            fun `200 - should return captured count when status filter is not provided`() {
                val user = mockAuthenticatedUser()
                val publicClient = PublicWordCaptureAPIClient(webClient)

                wordCaptureAPIClient.bulkCapture(TestData.APIRequestPayloads.createBulk, user)
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            TestData.activationReadyPublicItem("pending1"),
                            TestData.activationReadyPublicItem("pending2"),
                        ))
                )

                val response = wordCaptureAPIClient.getCapturedWords(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.data shouldHaveSize 5
                response.body.capturedCount shouldBe 5
            }

            @ParameterizedTest(name = "status={0}")
            @MethodSource("com.ord.controllers.words.TestWordCaptureController#statusFilterCases")
            fun `200 - should filter words by status query param`(
                status: WordStatus?,
                expectedTotal: Int,
                expectedCapturedCount: Long?,
                expectedItemStatus: WordStatus?,
            ) {
                val user = mockAuthenticatedUser()
                seedActiveAndCapturedWord(user)

                val response = wordCaptureAPIClient.getCapturedWords(
                    page = 0,
                    perPage = 50,
                    status = status,
                    user = user,
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize expectedTotal
                response.body.pagination.totalResults shouldBe expectedTotal.toLong()
                response.body.pagination.page shouldBe 0
                response.body.pagination.perPage shouldBe 50
                response.body.capturedCount shouldBe expectedCapturedCount

                if (expectedItemStatus != null) {
                    response.body.data.forEach { it.status shouldBe expectedItemStatus }
                } else {
                    response.body.data.map { it.status }.toSet() shouldBe setOf(WordStatus.ACTIVE, WordStatus.CAPTURED)
                }
            }

            private fun seedActiveAndCapturedWord(user: MockedAuthenticatedUser) {
                val publicClient = PublicWordCaptureAPIClient(webClient)

                wordsAPIClient.createWord(
                    CreateWordRequest(
                        sourceWord = TestData.TEST_WORD_1,
                        language = TestData.TEST_LANGUAGE,
                        translation = TestData.TEST_TRANSLATION,
                        definition = TestData.TEST_DEFINITION,
                        type = TestData.TEST_TYPE,
                    ),
                    user,
                )
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(
                                sourceWord = "pending",
                                language = TestData.TEST_LANGUAGE,
                            ),
                        ),
                    ),
                )
            }

            @Test
            fun `200 - overview counts should match filtered list totals`() {
                val user = mockAuthenticatedUser()
                val publicClient = PublicWordCaptureAPIClient(webClient)

                wordCaptureAPIClient.bulkCapture(
                    listOf(
                        CaptureWordRequest(sourceWord = "captured1", language = TestData.TEST_LANGUAGE),
                        CaptureWordRequest(sourceWord = "captured2", language = TestData.TEST_LANGUAGE),
                    ),
                    user,
                )
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            PublicCaptureWordItem(sourceWord = "pending1", language = TestData.TEST_LANGUAGE),
                            PublicCaptureWordItem(sourceWord = "pending2", language = TestData.TEST_LANGUAGE),
                        ),
                    ),
                )

                val overview = wordCaptureAPIClient.getOverview(user = user)
                val allWords = wordCaptureAPIClient.getCapturedWords(user = user)
                val activeWords = wordCaptureAPIClient.getCapturedWords(status = WordStatus.ACTIVE, user = user)
                val capturedWords = wordCaptureAPIClient.getCapturedWords(status = WordStatus.CAPTURED, user = user)

                overview.status shouldBe HttpStatus.OK
                overview.body!!.total shouldBe 4
                overview.body.activeCount shouldBe 0
                overview.body.capturedCount shouldBe 4

                allWords.body!!.pagination.totalResults shouldBe overview.body.total
                activeWords.body!!.pagination.totalResults shouldBe overview.body.activeCount
                capturedWords.body!!.pagination.totalResults shouldBe overview.body.capturedCount
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.getCapturedWords()
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/words/{id}/capture - update one quickly added word")
    inner class UpdateOneTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should update a quickly added word`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                val response = wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.sourceWord shouldBe "zaktualizowane"
                response.body!!.id shouldBe created.body!!.id
            }

            @Test
            fun `200 - updated word should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user
                )

                val wordInDb = wordRepository.findById(created.body!!.id).block()
                wordInDb!!.sourceWord shouldBe "zaktualizowane"
            }

            @Test
            fun `200 - should update all fields including translation, definition, extraMark, and type`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val response = wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOneWithAllFields,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.sourceWord shouldBe "zaktualizowane"
                response.body!!.translation shouldBe "updated"
                response.body!!.definition shouldBe "Updated definition"
                response.body!!.extraMark shouldBe WordExtraMark.OFFENSIVE
                response.body!!.type shouldBe WordType.VERB
            }

            @Test
            fun `200 - partial update should only change specified fields`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val partialUpdate = UpdateCapturedWordRequest(
                    sourceWord = null,
                    translation = null,
                    definition = "Only definition changed",
                    extraMark = null,
                    type = null
                )

                val response = wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = partialUpdate,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.sourceWord shouldBe TestData.TEST_WORD_1  // unchanged
                response.body!!.translation shouldBe TestData.TEST_TRANSLATION  // unchanged
                response.body!!.definition shouldBe "Only definition changed"  // changed
                response.body!!.extraMark shouldBe TestData.TEST_EXTRA_MARK  // unchanged
                response.body!!.type shouldBe TestData.TEST_TYPE  // unchanged
            }

            @Test
            fun `200 - should update only translation field`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val updateTranslationOnly = UpdateCapturedWordRequest(
                    sourceWord = null,
                    translation = "new translation",
                    definition = null,
                    extraMark = null,
                    type = null
                )

                val response = wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = updateTranslationOnly,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.sourceWord shouldBe TestData.TEST_WORD_1  // unchanged
                response.body!!.translation shouldBe "new translation"  // changed
                response.body!!.definition shouldBe TestData.TEST_DEFINITION  // unchanged
                response.body!!.extraMark shouldBe TestData.TEST_EXTRA_MARK  // unchanged
                response.body!!.type shouldBe TestData.TEST_TYPE  // unchanged
            }

            @Test
            fun `200 - updated translation should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val updateRequest = UpdateCapturedWordRequest(translation = "nueva traducción")

                wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = updateRequest,
                    user = user
                )

                val wordInDb = wordRepository.findById(created.body!!.id).block()
                wordInDb!!.translation shouldBe "nueva traducción"
                wordInDb.sourceWord shouldBe TestData.TEST_WORD_1  // unchanged
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.updateCaptured(
                    id = UUID.randomUUID(),
                    body = TestData.APIRequestPayloads.updateOne
                )
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should return 404 for non-existent word`() {
                val user = mockAuthenticatedUser()
                val response = wordCaptureAPIClient.updateCaptured(
                    id = UUID.randomUUID(),
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user
                )
                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should not allow updating another user's word`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user1)

                val response = wordCaptureAPIClient.updateCaptured(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user2
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - should reject empty updated word`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateCapturedWordRequest(sourceWord = "")

                val response = wordCaptureAPIClient.updateCaptured(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject updated word exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateCapturedWordRequest(sourceWord = "a".repeat(256))

                val response = wordCaptureAPIClient.updateCaptured(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject definition exceeding 2000 characters`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateCapturedWordRequest(definition = "a".repeat(2001))

                val response = wordCaptureAPIClient.updateCaptured(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject translation exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateCapturedWordRequest(translation = "a".repeat(256))

                val response = wordCaptureAPIClient.updateCaptured(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/words/bulk-update-source - update multiple quickly added words")
    inner class BulkUpdateTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should update multiple quickly added words`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.bulkCapture(TestData.APIRequestPayloads.createBulk, user)

                val updateMap = mapOf(
                    created.body!![0].id to "updated1",
                    created.body!![1].id to "updated2"
                )

                val response = wordCaptureAPIClient.bulkUpdateSource(updateMap, user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!! shouldHaveSize 2
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.bulkUpdateSource(mapOf())
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/words/activate-many - approve multiple quickly added words")
    inner class ActivateManyTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should activate multiple captured words`() {
                val user = mockAuthenticatedUser()

                // Create unapproved words via public endpoint
                val publicClient = PublicWordCaptureAPIClient(webClient)
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_1),
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_2),
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_3),
                        ),
                    ),
                )

                // Get the created words from database
                val wordsInDb = wordRepository.findAll().collectList().block()!!
                val idsToApprove = wordsInDb.map { it.id!! }

                // Approve the words
                val response = wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = idsToApprove),
                    user
                )

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - activated words should have ACTIVE status in database`() {
                val user = mockAuthenticatedUser()

                // Create unapproved words via public endpoint
                val publicClient = PublicWordCaptureAPIClient(webClient)
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_1),
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_2),
                        ),
                    ),
                )

                // Get the created words from database
                val wordsInDb = wordRepository.findAll().collectList().block()!!
                val idsToApprove = wordsInDb.map { it.id!! }

                // Approve the words
                wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = idsToApprove),
                    user
                )

                // Verify in database
                val approvedWords = wordRepository.findAllById(idsToApprove).collectList().block()!!
                approvedWords shouldHaveSize 2
                approvedWords.forEach { word ->
                    word.status shouldBe WordStatus.ACTIVE
                }
            }

            @Test
            fun `200 - should only activate words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                // Create unapproved words for user1
                val publicClient = PublicWordCaptureAPIClient(webClient)
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user1.email,
                        words = listOf(TestData.activationReadyPublicItem(TestData.TEST_WORD_1)),
                    ),
                )

                // Create unapproved words for user2
                publicClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user2.email,
                        words = listOf(TestData.activationReadyPublicItem(TestData.TEST_WORD_2)),
                    ),
                )

                // Get the created words from database
                val allWords = wordRepository.findAll().collectList().block()!!
                val user1WordId = allWords.find { it.sourceWord == TestData.TEST_WORD_1 }!!.id!!
                val user2WordId = allWords.find { it.sourceWord == TestData.TEST_WORD_2 }!!.id!!

                // User1 tries to approve both words
                wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = listOf(user1WordId, user2WordId)),
                    user1
                )

                // Only user1's word should be approved
                val user1Word = wordRepository.findById(user1WordId).block()!!
                val user2Word = wordRepository.findById(user2WordId).block()!!

                user1Word.status shouldBe WordStatus.ACTIVE
                user2Word.status shouldBe WordStatus.CAPTURED
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = listOf())
                )
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should reject empty IDs list`() {
                val user = mockAuthenticatedUser()
                val response = wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = listOf()),
                    user
                )
                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/words/{id} - delete captured word via CRUD")
    inner class DeleteViaCrudTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should delete a captured word`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                val response = wordsAPIClient.deleteWord(created.body!!.id, user)

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - deleted word should be removed from database`() {
                val user = mockAuthenticatedUser()
                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user)

                wordsAPIClient.deleteWord(created.body!!.id, user)

                val wordInDb = wordRepository.findById(created.body!!.id).block()
                wordInDb shouldBe null
            }

            @Test
            fun `200 - should only delete words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                val user1Word = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user1)
                val user2Word = wordCaptureAPIClient.captureOne(
                    CaptureWordRequest(sourceWord = TestData.TEST_WORD_2, language = TestData.TEST_LANGUAGE),
                    user2,
                )

                wordsAPIClient.deleteWord(user1Word.body!!.id, user1)

                val user1WordInDb = wordRepository.findById(user1Word.body!!.id).block()
                val user2WordInDb = wordRepository.findById(user2Word.body!!.id).block()

                user1WordInDb shouldBe null
                user2WordInDb shouldNotBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordsAPIClient.deleteWord(UUID.randomUUID())
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should return 404 for non-existent word`() {
                val user = mockAuthenticatedUser()
                val response = wordsAPIClient.deleteWord(UUID.randomUUID(), user)
                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should not allow deleting another user's word`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                val created = wordCaptureAPIClient.captureOne(TestData.APIRequestPayloads.createOne, user1)

                val response = wordsAPIClient.deleteWord(created.body!!.id, user2)

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
}