package com.ord.controllers.quickly_added_words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.quickly_added_words.api.requests.ApproveManyQAWRequest
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem
import com.ord.features.quickly_added_words.api.requests.UpdateQAWRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
import com.ord.features.quickly_added_words.repositories.QAWRepository
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.testing_utils.api.clients.QAWAPIClient
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
import java.util.*

@DisplayName("- QuicklyAddedWordsController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestQuicklyAddedWordsController @Autowired constructor(
    private val qawRepository: QAWRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    _userRepository: UserRepository,
    _otpCodeRepository: OtpCodeRepository,
    _passwordEncoder: PasswordEncoder
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = _userRepository,
    otpCodeRepository = _otpCodeRepository,
    passwordEncoder = _passwordEncoder
) {
    private val qawAPIClient = QAWAPIClient(webClient)

    object TestData {
        const val TEST_WORD_1 = "przykład"
        const val TEST_WORD_2 = "słowo"
        const val TEST_WORD_3 = "test"
        const val TEST_TRANSLATION = "example"
        const val TEST_DEFINITION = "A sample word for testing"
        val TEST_LANGUAGE = LanguageName.POLISH
        val TEST_EXTRA_MARK = WordExtraMark.SLANG
        val TEST_TYPE = WordType.NOUN

        object APIRequestPayloads {
            val createOne = CreateQAWRequest(
                word = TEST_WORD_1,
                language = TEST_LANGUAGE
            )

            val createOneWithAllFields = CreateQAWRequest(
                word = TEST_WORD_1,
                language = TEST_LANGUAGE,
                translation = TEST_TRANSLATION,
                definition = TEST_DEFINITION,
                extraMark = TEST_EXTRA_MARK,
                type = TEST_TYPE
            )

            val createBulk = listOf(
                CreateQAWRequest(word = TEST_WORD_1, language = TEST_LANGUAGE),
                CreateQAWRequest(word = TEST_WORD_2, language = TEST_LANGUAGE),
                CreateQAWRequest(word = TEST_WORD_3, language = TEST_LANGUAGE)
            )

            val updateOne = UpdateQAWRequest(
                updatedWord = "zaktualizowane"
            )

            val updateOneWithAllFields = UpdateQAWRequest(
                updatedWord = "zaktualizowane",
                translation = "updated",
                definition = "Updated definition",
                extraMark = WordExtraMark.OFFENSIVE,
                type = WordType.VERB
            )
        }
    }

    @AfterEach
    fun cleanup() {
        qawRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[POST] /api/v1/quickly-added-words/ - create one quickly added word")
    inner class CreateOneTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var response: APIClientResponse<QuicklyAddedWordDTO?>

            @BeforeEach
            fun beforeEach() {
                val user = mockAuthenticatedUser()
                response = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)
            }

            @Test
            fun `201 - should create a quickly added word`() {
                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.word shouldBe TestData.TEST_WORD_1
                response.body!!.language shouldBe TestData.TEST_LANGUAGE
                response.body!!.id shouldNotBe null
            }

            @Test
            fun `201 - created word should be persisted in database`() {
                val wordInDb = qawRepository.findById(response.body!!.id).block()
                wordInDb shouldNotBe null
                wordInDb!!.word shouldBe TestData.TEST_WORD_1
            }

            @Test
            fun `201 - created word should be approved by default`() {
                response.body!!.isApproved shouldBe true
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should reject empty word`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = "",
                    language = TestData.TEST_LANGUAGE
                )

                val response = qawAPIClient.createOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject blank word`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = "   ",
                    language = TestData.TEST_LANGUAGE
                )

                val response = qawAPIClient.createOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject word exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = "a".repeat(256),
                    language = TestData.TEST_LANGUAGE
                )

                val response = qawAPIClient.createOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject definition exceeding 2000 characters`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = "test",
                    language = TestData.TEST_LANGUAGE,
                    definition = "a".repeat(2001)
                )

                val response = qawAPIClient.createOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject translation exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = "test",
                    language = TestData.TEST_LANGUAGE,
                    translation = "a".repeat(256)
                )

                val response = qawAPIClient.createOne(request, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/quickly-added-words/ - create one quickly added word with translation")
    inner class CreateOneWithTranslationTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create a word with only translation (no definition)`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = TestData.TEST_WORD_1,
                    language = TestData.TEST_LANGUAGE,
                    translation = TestData.TEST_TRANSLATION
                )

                val response = qawAPIClient.createOne(request, user)

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.word shouldBe TestData.TEST_WORD_1
                response.body!!.translation shouldBe TestData.TEST_TRANSLATION
                response.body!!.definition shouldBe null
            }

            @Test
            fun `201 - word with translation should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val request = CreateQAWRequest(
                    word = TestData.TEST_WORD_1,
                    language = TestData.TEST_LANGUAGE,
                    translation = TestData.TEST_TRANSLATION
                )

                val response = qawAPIClient.createOne(request, user)

                val wordInDb = qawRepository.findById(response.body!!.id).block()
                wordInDb shouldNotBe null
                wordInDb!!.translation shouldBe TestData.TEST_TRANSLATION
                wordInDb.definition shouldBe null
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/quickly-added-words/ - create one quickly added word with all fields")
    inner class CreateOneWithAllFieldsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create a quickly added word with translation, definition, extraMark, and type`() {
                val user = mockAuthenticatedUser()
                val response = qawAPIClient.createOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.word shouldBe TestData.TEST_WORD_1
                response.body!!.language shouldBe TestData.TEST_LANGUAGE
                response.body!!.translation shouldBe TestData.TEST_TRANSLATION
                response.body!!.definition shouldBe TestData.TEST_DEFINITION
                response.body!!.extraMark shouldBe TestData.TEST_EXTRA_MARK
                response.body!!.type shouldBe TestData.TEST_TYPE
            }

            @Test
            fun `201 - created word with all fields should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val response = qawAPIClient.createOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val wordInDb = qawRepository.findById(response.body!!.id).block()
                wordInDb shouldNotBe null
                wordInDb!!.translation shouldBe TestData.TEST_TRANSLATION
                wordInDb.definition shouldBe TestData.TEST_DEFINITION
                wordInDb.extraMark shouldBe TestData.TEST_EXTRA_MARK
                wordInDb.type shouldBe TestData.TEST_TYPE
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/quickly-added-words/bulk-create - create multiple quickly added words")
    inner class BulkCreateTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var response: APIClientResponse<List<QuicklyAddedWordDTO>?>

            @BeforeEach
            fun beforeEach() {
                val user = mockAuthenticatedUser()
                response = qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk, user)
            }

            @Test
            fun `201 - should create multiple quickly added words`() {
                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!! shouldHaveSize 3
                response.body!!.map { it.word } shouldContain TestData.TEST_WORD_1
                response.body!!.map { it.word } shouldContain TestData.TEST_WORD_2
                response.body!!.map { it.word } shouldContain TestData.TEST_WORD_3
            }

            @Test
            fun `201 - all created words should be persisted in database`() {
                val wordsInDb = qawRepository.findAll().collectList().block()!!
                wordsInDb shouldHaveSize 3
            }

            @Test
            fun `201 - all created words should be approved by default`() {
                response.body!!.forEach { word ->
                    word.isApproved shouldBe true
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/quickly-added-words/ - get many quickly added words")
    inner class GetManyQAWsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return paginated list of quickly added words`() {
                val user = mockAuthenticatedUser()
                qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk, user)

                val response = qawAPIClient.getManyQAWs(user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 3
                response.body.pagination.totalResults shouldBe 3
            }

            @Test
            fun `200 - should respect pagination parameters`() {
                val user = mockAuthenticatedUser()
                qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk, user)

                val response = qawAPIClient.getManyQAWs(page = 1, perPage = 2, user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 2
                response.body.pagination.totalResults shouldBe 3
                response.body.pagination.page shouldBe 1
                response.body.pagination.perPage shouldBe 2
            }

            @Test
            fun `200 - should only return words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user1)
                qawAPIClient.createOne(
                    CreateQAWRequest(word = "other", language = TestData.TEST_LANGUAGE),
                    user2
                )

                val response = qawAPIClient.getManyQAWs(user = user1)

                response.body!!.data shouldHaveSize 1
                response.body.data[0].word shouldBe TestData.TEST_WORD_1
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.getManyQAWs()
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/quickly-added-words/{id} - update one quickly added word")
    inner class UpdateOneTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should update a quickly added word`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                val response = qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.word shouldBe "zaktualizowane"
                response.body!!.id shouldBe created.body!!.id
            }

            @Test
            fun `200 - updated word should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user
                )

                val wordInDb = qawRepository.findById(created.body!!.id).block()
                wordInDb!!.word shouldBe "zaktualizowane"
            }

            @Test
            fun `200 - should update all fields including translation, definition, extraMark, and type`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val response = qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOneWithAllFields,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.word shouldBe "zaktualizowane"
                response.body!!.translation shouldBe "updated"
                response.body!!.definition shouldBe "Updated definition"
                response.body!!.extraMark shouldBe WordExtraMark.OFFENSIVE
                response.body!!.type shouldBe WordType.VERB
            }

            @Test
            fun `200 - partial update should only change specified fields`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val partialUpdate = UpdateQAWRequest(
                    updatedWord = null,
                    translation = null,
                    definition = "Only definition changed",
                    extraMark = null,
                    type = null
                )

                val response = qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = partialUpdate,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.word shouldBe TestData.TEST_WORD_1  // unchanged
                response.body!!.translation shouldBe TestData.TEST_TRANSLATION  // unchanged
                response.body!!.definition shouldBe "Only definition changed"  // changed
                response.body!!.extraMark shouldBe TestData.TEST_EXTRA_MARK  // unchanged
                response.body!!.type shouldBe TestData.TEST_TYPE  // unchanged
            }

            @Test
            fun `200 - should update only translation field`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val updateTranslationOnly = UpdateQAWRequest(
                    updatedWord = null,
                    translation = "new translation",
                    definition = null,
                    extraMark = null,
                    type = null
                )

                val response = qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = updateTranslationOnly,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.word shouldBe TestData.TEST_WORD_1  // unchanged
                response.body!!.translation shouldBe "new translation"  // changed
                response.body!!.definition shouldBe TestData.TEST_DEFINITION  // unchanged
                response.body!!.extraMark shouldBe TestData.TEST_EXTRA_MARK  // unchanged
                response.body!!.type shouldBe TestData.TEST_TYPE  // unchanged
            }

            @Test
            fun `200 - updated translation should be persisted in database`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOneWithAllFields, user)

                val updateRequest = UpdateQAWRequest(translation = "nueva traducción")

                qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = updateRequest,
                    user = user
                )

                val wordInDb = qawRepository.findById(created.body!!.id).block()
                wordInDb!!.translation shouldBe "nueva traducción"
                wordInDb.word shouldBe TestData.TEST_WORD_1  // unchanged
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.updateOne(
                    id = UUID.randomUUID(),
                    body = TestData.APIRequestPayloads.updateOne
                )
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should return 404 for non-existent word`() {
                val user = mockAuthenticatedUser()
                val response = qawAPIClient.updateOne(
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

                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user1)

                val response = qawAPIClient.updateOne(
                    id = created.body!!.id,
                    body = TestData.APIRequestPayloads.updateOne,
                    user = user2
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - should reject empty updated word`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateQAWRequest(updatedWord = "")

                val response = qawAPIClient.updateOne(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject updated word exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateQAWRequest(updatedWord = "a".repeat(256))

                val response = qawAPIClient.updateOne(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject definition exceeding 2000 characters`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateQAWRequest(definition = "a".repeat(2001))

                val response = qawAPIClient.updateOne(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - should reject translation exceeding 255 characters`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                val updateRequest = UpdateQAWRequest(translation = "a".repeat(256))

                val response = qawAPIClient.updateOne(created.body!!.id, updateRequest, user)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/quickly-added-words/bulk-update - update multiple quickly added words")
    inner class BulkUpdateTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should update multiple quickly added words`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk, user)

                val updateMap = mapOf(
                    created.body!![0].id to "updated1",
                    created.body!![1].id to "updated2"
                )

                val response = qawAPIClient.bulkUpdate(updateMap, user)

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
                val response = qawAPIClient.bulkUpdate(mapOf())
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/quickly-added-words/approve-many - approve multiple quickly added words")
    inner class ApproveManyTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should approve multiple unapproved words`() {
                val user = mockAuthenticatedUser()

                // Create unapproved words via public endpoint
                val publicClient = com.ord.testing_utils.api.clients.PublicQAWAPIClient(webClient)
                publicClient.publicBulkCreate(
                    com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest(
                        userEmail = user.email,
                        words = listOf(
                            com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem(word = TestData.TEST_WORD_1),
                            com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem(word = TestData.TEST_WORD_2),
                            com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem(word = TestData.TEST_WORD_3)
                        ),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                // Get the created words from database
                val wordsInDb = qawRepository.findAll().collectList().block()!!
                val idsToApprove = wordsInDb.map { it.id!! }

                // Approve the words
                val response = qawAPIClient.approveMany(
                    ApproveManyQAWRequest(ids = idsToApprove),
                    user
                )

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - approved words should have isApproved=true in database`() {
                val user = mockAuthenticatedUser()

                // Create unapproved words via public endpoint
                val publicClient = com.ord.testing_utils.api.clients.PublicQAWAPIClient(webClient)
                publicClient.publicBulkCreate(
                    com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest(
                        userEmail = user.email,
                        words = listOf(
                            com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem(word = TestData.TEST_WORD_1),
                            com.ord.features.quickly_added_words.api.requests.PublicQAWWordItem(word = TestData.TEST_WORD_2)
                        ),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                // Get the created words from database
                val wordsInDb = qawRepository.findAll().collectList().block()!!
                val idsToApprove = wordsInDb.map { it.id!! }

                // Approve the words
                qawAPIClient.approveMany(
                    ApproveManyQAWRequest(ids = idsToApprove),
                    user
                )

                // Verify in database
                val approvedWords = qawRepository.findAllById(idsToApprove).collectList().block()!!
                approvedWords shouldHaveSize 2
                approvedWords.forEach { word ->
                    word.isApproved shouldBe true
                }
            }

            @Test
            fun `200 - should only approve words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                // Create unapproved words for user1
                val publicClient = com.ord.testing_utils.api.clients.PublicQAWAPIClient(webClient)
                publicClient.publicBulkCreate(
                    com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest(
                        userEmail = user1.email,
                        words = listOf(
                            PublicQAWWordItem(word = TestData.TEST_WORD_1)
                        ),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                // Create unapproved words for user2
                publicClient.publicBulkCreate(
                    com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest(
                        userEmail = user2.email,
                        words = listOf(
                            PublicQAWWordItem(word = TestData.TEST_WORD_2)
                        ),
                        language = TestData.TEST_LANGUAGE
                    )
                )

                // Get the created words from database
                val allWords = qawRepository.findAll().collectList().block()!!
                val user1WordId = allWords.find { it.word == TestData.TEST_WORD_1 }!!.id!!
                val user2WordId = allWords.find { it.word == TestData.TEST_WORD_2 }!!.id!!

                // User1 tries to approve both words
                qawAPIClient.approveMany(
                    ApproveManyQAWRequest(ids = listOf(user1WordId, user2WordId)),
                    user1
                )

                // Only user1's word should be approved
                val user1Word = qawRepository.findById(user1WordId).block()!!
                val user2Word = qawRepository.findById(user2WordId).block()!!

                user1Word.isApproved shouldBe true
                user2Word.isApproved shouldBe false
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.approveMany(
                    ApproveManyQAWRequest(ids = listOf())
                )
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should reject empty IDs list`() {
                val user = mockAuthenticatedUser()
                val response = qawAPIClient.approveMany(
                    ApproveManyQAWRequest(ids = listOf()),
                    user
                )
                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/quickly-added-words/{id} - delete one quickly added word")
    inner class DeleteOneTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should delete a quickly added word`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                val response = qawAPIClient.deleteOne(created.body!!.id, user)

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - deleted word should be removed from database`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user)

                qawAPIClient.deleteOne(created.body!!.id, user)

                val wordInDb = qawRepository.findById(created.body!!.id).block()
                wordInDb shouldBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.deleteOne(UUID.randomUUID())
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should return 404 for non-existent word`() {
                val user = mockAuthenticatedUser()
                val response = qawAPIClient.deleteOne(UUID.randomUUID(), user)
                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should not allow deleting another user's word`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                val created = qawAPIClient.createOne(TestData.APIRequestPayloads.createOne, user1)

                val response = qawAPIClient.deleteOne(created.body!!.id, user2)

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/quickly-added-words/bulk-delete - delete multiple quickly added words")
    inner class BulkDeleteTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should delete multiple quickly added words`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk, user)

                val idsToDelete = listOf(
                    created.body!![0].id,
                    created.body!![1].id
                )

                val response = qawAPIClient.bulkDelete(idsToDelete, user)

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - deleted words should be removed from database`() {
                val user = mockAuthenticatedUser()
                val created = qawAPIClient.bulkCreate(TestData.APIRequestPayloads.createBulk, user)

                val idsToDelete = listOf(
                    created.body!![0].id,
                    created.body!![1].id
                )

                qawAPIClient.bulkDelete(idsToDelete, user)

                val remainingWords = qawRepository.findAll().collectList().block()!!
                remainingWords shouldHaveSize 1
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = qawAPIClient.bulkDelete(listOf())
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }
}