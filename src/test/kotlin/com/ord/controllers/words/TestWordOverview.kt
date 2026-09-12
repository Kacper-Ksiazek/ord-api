package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.enums.WordToggleableProperty
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.repositories.WordRepository
import com.ord.testing_utils.api.clients.WordsAPIClient
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

@DisplayName("- WordCRUDController: Overview")
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
    private val wordsAPIClient = WordsAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        wordRepository.deleteAll().block()
    }

    private fun createWordRequest(sourceWord: String, language: LanguageName = LanguageName.POLISH) =
        CreateWordRequest(
            type = WordType.NOUN,
            sourceWord = sourceWord,
            translation = "translation-$sourceWord",
            definition = "definition-$sourceWord",
            language = language,
        )

    @Nested
    @DisplayName("[GET] /api/v1/words/overview")
    inner class GetOverview {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = wordsAPIClient.getOverview(user = null)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return zero counts for user with no words`() {
                val user = mockAuthenticatedUser()

                val response = wordsAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 0
                response.body.bookmarkedCount shouldBe 0
            }

            @Test
            fun `200 - should return total count for active words`() {
                val user = mockAuthenticatedUser()

                repeat(5) { index ->
                    wordsAPIClient.createWord(createWordRequest("word-$index"), user = user)
                }

                val response = wordsAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 5
                response.body.bookmarkedCount shouldBe 0
            }

            @Test
            fun `200 - should only count words belonging to the authenticated user`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()

                wordsAPIClient.createWord(createWordRequest("user-a-word"), user = userA)
                wordsAPIClient.createWord(createWordRequest("user-b-word"), user = userB)

                val responseA = wordsAPIClient.getOverview(user = userA)
                val responseB = wordsAPIClient.getOverview(user = userB)

                responseA.status shouldBe HttpStatus.OK
                responseA.body!!.total shouldBe 1

                responseB.status shouldBe HttpStatus.OK
                responseB.body!!.total shouldBe 1
            }

            @Test
            fun `200 - should scope counts to requested language`() {
                val user = mockAuthenticatedUser()

                wordsAPIClient.createWord(
                    createWordRequest("english-word", LanguageName.ENGLISH),
                    user = user,
                )
                wordsAPIClient.createWord(
                    createWordRequest("polish-word", LanguageName.POLISH),
                    user = user,
                )

                val englishOverview = wordsAPIClient.getOverview(
                    user = user,
                    language = LanguageName.ENGLISH,
                )
                val polishOverview = wordsAPIClient.getOverview(
                    user = user,
                    language = LanguageName.POLISH,
                )

                englishOverview.status shouldBe HttpStatus.OK
                englishOverview.body!!.total shouldBe 1

                polishOverview.status shouldBe HttpStatus.OK
                polishOverview.body!!.total shouldBe 1
            }

            @Test
            fun `200 - should count bookmarked words`() {
                val user = mockAuthenticatedUser()

                val created = wordsAPIClient.createWord(createWordRequest("bookmark-me"), user = user)
                wordsAPIClient.createWord(createWordRequest("plain-word"), user = user)
                wordsAPIClient.togglePropertyForOneWord(
                    id = created.body!!.id,
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    user = user,
                )

                val response = wordsAPIClient.getOverview(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.total shouldBe 2
                response.body.bookmarkedCount shouldBe 1
            }
        }
    }
}
