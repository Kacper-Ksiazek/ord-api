package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.word.api.ai.requests.dto.WordFillGapsItem
import com.ord.core.word.api.ai.requests.dto.WordFillGapsRequest
import com.ord.core.word.api.ai.responses.dto.WordFillGapsResponse
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.testing_utils.api.clients.WordFillGapsAIAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
@DisplayName("- WordFillGapsAIController")
class TestWordFillGapsAIController @Autowired constructor(
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
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
    private val wordFillGapsAIAPIClient = WordFillGapsAIAPIClient(webClient)

    private fun fillGapsExpectingSuccess(
        body: WordFillGapsRequest,
    ): APIClientResponse<WordFillGapsResponse?> {
        val maxAttempts = if (System.getProperty("INTEGRATION_TESTS") == "true") 3 else 1
        var lastResponse: APIClientResponse<WordFillGapsResponse?>? = null

        repeat(maxAttempts) { attempt ->
            val response = wordFillGapsAIAPIClient.fillGaps(body, authenticatedUser)
            lastResponse = response
            val items = response.body?.items
            if (response.status == HttpStatus.OK && items != null && items.all { it.error == null }) {
                return response
            }
            if (attempt < maxAttempts - 1) {
                Thread.sleep(1_000)
            }
        }

        return lastResponse!!
    }

    lateinit var authenticatedUser: MockedAuthenticatedUser

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser(
            languages = mapOf(
                LanguageName.ENGLISH to LanguageProficiencyLevel.C1,
                LanguageName.NORWEGIAN to LanguageProficiencyLevel.B2,
            ),
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/ai/fill-gaps")
    inner class FillGaps {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should fill gaps for a single word`() {
                val response = fillGapsExpectingSuccess(
                    body = WordFillGapsRequest(
                        language = LanguageName.ENGLISH,
                        items = listOf(WordFillGapsItem(sourceWord = "verbose")),
                    ),
                )

                response.status shouldBe HttpStatus.OK
                val body = response.body.shouldNotBeNull()
                body.items shouldHaveSize 1

                val item = body.items.first()
                item.inputSourceWord shouldBe "verbose"
                item.error.shouldBeNull()
                item.sourceWord.shouldNotBeNull().shouldNotBeBlank()
                item.translation.shouldNotBeNull().shouldNotBeBlank()
                item.definition.shouldNotBeNull().shouldNotBeBlank()
                item.type shouldBe WordType.ADJECTIVE

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_FILL_GAPS")
            }

            @Test
            fun `200 - should fill gaps for multiple words in order`() {
                val inputWords = listOf("hello", "apple", "house")
                val response = fillGapsExpectingSuccess(
                    body = WordFillGapsRequest(
                        language = LanguageName.ENGLISH,
                        items = inputWords.map { WordFillGapsItem(sourceWord = it) },
                    ),
                )

                response.status shouldBe HttpStatus.OK
                val body = response.body.shouldNotBeNull()
                body.items shouldHaveSize 3
                body.items.map { it.inputSourceWord } shouldBe inputWords
                body.items.forEach { item ->
                    item.error.shouldBeNull()
                    item.translation.shouldNotBeNull().shouldNotBeBlank()
                    item.definition.shouldNotBeNull().shouldNotBeBlank()
                    item.type.shouldNotBeNull()
                }

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_FILL_GAPS")
            }

            @Test
            fun `200 - should accept optional known fields in request items`() {
                val response = fillGapsExpectingSuccess(
                    body = WordFillGapsRequest(
                        language = LanguageName.ENGLISH,
                        items = listOf(
                            WordFillGapsItem(
                                sourceWord = "dude",
                                translation = "stary",
                                type = WordType.NOUN,
                                extraMark = WordExtraMark.SLANG,
                            ),
                        ),
                    ),
                )

                response.status shouldBe HttpStatus.OK
                val body = response.body.shouldNotBeNull()
                body.items shouldHaveSize 1

                val item = body.items.first()
                item.inputSourceWord shouldBe "dude"
                item.error.shouldBeNull()
                item.translation shouldBe "stary"
                item.type shouldBe WordType.NOUN
                item.extraMark shouldBe WordExtraMark.SLANG
                item.definition.shouldNotBeNull().shouldNotBeBlank()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_FILL_GAPS")
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot fill gaps`() {
                val response = wordFillGapsAIAPIClient.fillGaps(
                    body = WordFillGapsRequest(
                        language = LanguageName.ENGLISH,
                        items = listOf(WordFillGapsItem(sourceWord = "hello")),
                    ),
                    user = null,
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - user without proficiency in requested language`() {
                val response = wordFillGapsAIAPIClient.fillGaps(
                    body = WordFillGapsRequest(
                        language = LanguageName.SPANISH,
                        items = listOf(WordFillGapsItem(sourceWord = "hola")),
                    ),
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - empty items list`() {
                val response = wordFillGapsAIAPIClient.fillGaps(
                    body = WordFillGapsRequest(
                        language = LanguageName.ENGLISH,
                        items = emptyList(),
                    ),
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - more than 20 items`() {
                val items = (1..21).map { WordFillGapsItem(sourceWord = "word$it") }

                val response = wordFillGapsAIAPIClient.fillGaps(
                    body = WordFillGapsRequest(
                        language = LanguageName.ENGLISH,
                        items = items,
                    ),
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}
