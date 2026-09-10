package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.PublicCaptureWordItem
import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.repositories.WordProgressRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.testing_utils.api.clients.PublicWordCaptureAPIClient
import com.ord.testing_utils.api.clients.WordCaptureAPIClient
import com.ord.testing_utils.api.clients.WordsAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import java.util.stream.Stream

@DisplayName("- WordCaptureController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestWordCaptureController @Autowired constructor(
    private val wordRepository: WordRepository,
    private val wordProgressRepository: WordProgressRepository,
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
    private val publicWordCaptureAPIClient = PublicWordCaptureAPIClient(webClient)

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
    }

    @AfterEach
    fun cleanup() {
        wordRepository.deleteAll().block()
    }

    companion object {
        @JvmStatic
        fun progressFilterCases(): Stream<Arguments> = Stream.of(
            Arguments.of(true, 1, null as Long?, true),
            Arguments.of(false, 1, null as Long?, false),
            Arguments.of(null as Boolean?, 2, 1L, null as Boolean?),
        )
    }

    private fun seedPublicPendingWords(user: MockedAuthenticatedUser, sourceWords: List<String>) {
        publicWordCaptureAPIClient.publicBulkCreate(
            PublicWordsBulkCaptureRequest(
                userEmail = user.email,
                words = sourceWords.map {
                    PublicCaptureWordItem(sourceWord = it, language = TestData.TEST_LANGUAGE)
                },
            ),
        )
    }

    private fun firstPendingWordId(user: MockedAuthenticatedUser): UUID {
        return wordCaptureAPIClient.listWords(
            language = TestData.TEST_LANGUAGE,
            hasProgress = false,
            user = user,
        ).body!!.data.first().id!!
    }

    @Nested
    @DisplayName("[GET] /api/v1/words - list words")
    inner class ListWordsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return paginated list of pending words`() {
                val user = mockAuthenticatedUser()
                seedPublicPendingWords(user, listOf(TestData.TEST_WORD_1, TestData.TEST_WORD_2, TestData.TEST_WORD_3))

                val response = wordCaptureAPIClient.listWords(language = TestData.TEST_LANGUAGE, user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 3
                response.body.pagination.totalResults shouldBe 3
            }

            @Test
            fun `200 - should respect pagination parameters`() {
                val user = mockAuthenticatedUser()
                seedPublicPendingWords(user, listOf(TestData.TEST_WORD_1, TestData.TEST_WORD_2, TestData.TEST_WORD_3))

                val response = wordCaptureAPIClient.listWords(
                    language = TestData.TEST_LANGUAGE,
                    page = 0,
                    perPage = 2,
                    user = user,
                )

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
                seedPublicPendingWords(user, (1..51).map { "word-$it" })

                val firstPage = wordCaptureAPIClient.listWords(
                    language = TestData.TEST_LANGUAGE,
                    page = 0,
                    perPage = 50,
                    user = user,
                )
                val secondPage = wordCaptureAPIClient.listWords(
                    language = TestData.TEST_LANGUAGE,
                    page = 1,
                    perPage = 50,
                    user = user,
                )

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

                seedPublicPendingWords(user1, listOf(TestData.TEST_WORD_1))
                seedPublicPendingWords(user2, listOf("other"))

                val response = wordCaptureAPIClient.listWords(language = TestData.TEST_LANGUAGE, user = user1)

                response.body!!.data shouldHaveSize 1
                response.body.data[0].sourceWord shouldBe TestData.TEST_WORD_1
            }

            @Test
            fun `200 - should return unverified source count when filter is not provided`() {
                val user = mockAuthenticatedUser()

                seedPublicPendingWords(user, listOf(TestData.TEST_WORD_1, TestData.TEST_WORD_2, TestData.TEST_WORD_3))
                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            TestData.activationReadyPublicItem("pending1"),
                            TestData.activationReadyPublicItem("pending2"),
                        ),
                    ),
                )

                val response = wordCaptureAPIClient.listWords(language = TestData.TEST_LANGUAGE, user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.data shouldHaveSize 5
                response.body.unverifiedSourceCount shouldBe 5
            }

            @ParameterizedTest(name = "hasProgress={0}")
            @MethodSource("com.ord.controllers.words.TestWordCaptureController#progressFilterCases")
            fun `200 - should filter words by hasProgress query param`(
                hasProgress: Boolean?,
                expectedTotal: Int,
                expectedUnverifiedSourceCount: Long?,
                expectedHasProgress: Boolean?,
            ) {
                val user = mockAuthenticatedUser()
                seedActiveAndInboxWord(user)

                val response = wordCaptureAPIClient.listWords(
                    language = TestData.TEST_LANGUAGE,
                    page = 0,
                    perPage = 50,
                    hasProgress = hasProgress,
                    user = user,
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize expectedTotal
                response.body.pagination.totalResults shouldBe expectedTotal.toLong()
                response.body.pagination.page shouldBe 0
                response.body.pagination.perPage shouldBe 50
                response.body.unverifiedSourceCount shouldBe expectedUnverifiedSourceCount

                if (expectedHasProgress != null) {
                    response.body.data.forEach {
                        if (expectedHasProgress) it.progress shouldNotBe null else it.progress shouldBe null
                    }
                } else {
                    response.body.data.any { it.progress != null } shouldBe true
                    response.body.data.any { it.progress == null } shouldBe true
                }
            }

            private fun seedActiveAndInboxWord(user: MockedAuthenticatedUser) {
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
                publicWordCaptureAPIClient.publicBulkCreate(
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

                seedPublicPendingWords(user, listOf("pending1", "pending2", "pending3", "pending4"))

                val overview = wordCaptureAPIClient.getOverview(user = user)
                val allWords = wordCaptureAPIClient.listWords(language = TestData.TEST_LANGUAGE, user = user)
                val activeWords = wordCaptureAPIClient.listWords(
                    language = TestData.TEST_LANGUAGE,
                    hasProgress = true,
                    user = user,
                )
                val inboxWords = wordCaptureAPIClient.listWords(
                    language = TestData.TEST_LANGUAGE,
                    hasProgress = false,
                    user = user,
                )

                overview.status shouldBe HttpStatus.OK
                overview.body!!.total shouldBe 4
                overview.body.activeCount shouldBe 0
                overview.body.pendingCount shouldBe 4
                overview.body.unverifiedSourceCount shouldBe 4

                allWords.body!!.pagination.totalResults shouldBe overview.body.total
                activeWords.body!!.pagination.totalResults shouldBe overview.body.activeCount
                inboxWords.body!!.pagination.totalResults shouldBe overview.body.pendingCount
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should require authentication`() {
                val response = wordCaptureAPIClient.listWords(language = TestData.TEST_LANGUAGE)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should require language`() {
                val user = mockAuthenticatedUser()
                val response = wordCaptureAPIClient.listWords(language = null, user = user)
                response.status shouldBe HttpStatus.BAD_REQUEST
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

                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_1),
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_2),
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_3),
                        ),
                    ),
                )

                val wordsInDb = wordRepository.findAllByUserId(user.userInfo.id).collectList().block()!!
                val idsToApprove = wordsInDb.map { it.id!! }

                val response = wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = idsToApprove),
                    user
                )

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - activated words should have learning progress in database`() {
                val user = mockAuthenticatedUser()

                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user.email,
                        words = listOf(
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_1),
                            TestData.activationReadyPublicItem(TestData.TEST_WORD_2),
                        ),
                    ),
                )

                val wordsInDb = wordRepository.findAllByUserId(user.userInfo.id).collectList().block()!!
                val idsToApprove = wordsInDb.map { it.id!! }

                wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = idsToApprove),
                    user
                )

                val approvedWords = wordRepository.findAllById(idsToApprove).collectList().block()!!
                approvedWords shouldHaveSize 2
                approvedWords.forEach { word ->
                    word.isFromUnverifiedSource shouldBe false
                    wordProgressRepository.findByWordIdAndUserId(word.id!!, user.userInfo.id).block() shouldNotBe null
                }
            }

            @Test
            fun `200 - should only activate words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user1.email,
                        words = listOf(TestData.activationReadyPublicItem(TestData.TEST_WORD_1)),
                    ),
                )
                publicWordCaptureAPIClient.publicBulkCreate(
                    PublicWordsBulkCaptureRequest(
                        userEmail = user2.email,
                        words = listOf(TestData.activationReadyPublicItem(TestData.TEST_WORD_2)),
                    ),
                )

                val user1WordId = wordRepository.findAllByUserId(user1.userInfo.id).collectList().block()!!.single().id!!
                val user2WordId = wordRepository.findAllByUserId(user2.userInfo.id).collectList().block()!!.single().id!!

                wordCaptureAPIClient.activateMany(
                    ActivateManyWordsRequest(ids = listOf(user1WordId, user2WordId)),
                    user1
                )

                val user1Word = wordRepository.findById(user1WordId).block()!!
                val user2Word = wordRepository.findById(user2WordId).block()!!

                user1Word.isFromUnverifiedSource shouldBe false
                wordProgressRepository.findByWordIdAndUserId(user1WordId, user1.userInfo.id).block() shouldNotBe null
                user2Word.isFromUnverifiedSource shouldBe true
                wordProgressRepository.findByWordIdAndUserId(user2WordId, user2.userInfo.id).block() shouldBe null
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
                seedPublicPendingWords(user, listOf(TestData.TEST_WORD_1))
                val wordId = firstPendingWordId(user)

                val response = wordsAPIClient.deleteWord(wordId, user)

                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - deleted word should be removed from database`() {
                val user = mockAuthenticatedUser()
                seedPublicPendingWords(user, listOf(TestData.TEST_WORD_1))
                val wordId = firstPendingWordId(user)

                wordsAPIClient.deleteWord(wordId, user)

                val wordInDb = wordRepository.findById(wordId).block()
                wordInDb shouldBe null
            }

            @Test
            fun `200 - should only delete words belonging to the user`() {
                val user1 = mockAuthenticatedUser()
                val user2 = mockAuthenticatedUser()

                seedPublicPendingWords(user1, listOf(TestData.TEST_WORD_1))
                seedPublicPendingWords(user2, listOf(TestData.TEST_WORD_2))

                val user1WordId = firstPendingWordId(user1)
                val user2WordId = firstPendingWordId(user2)

                wordsAPIClient.deleteWord(user1WordId, user1)

                val user1WordInDb = wordRepository.findById(user1WordId).block()
                val user2WordInDb = wordRepository.findById(user2WordId).block()

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

                seedPublicPendingWords(user1, listOf(TestData.TEST_WORD_1))
                val wordId = firstPendingWordId(user1)

                val response = wordsAPIClient.deleteWord(wordId, user2)

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
}
