package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import com.ord.testing_utils.api.clients.WordAIAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
@DisplayName("- WordAIController")
class TestWordAIController @Autowired constructor(
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
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
    private val wordAIAPIClient = WordAIAPIClient(webClient)

    lateinit var authenticatedUser: MockedAuthenticatedUser

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser(
            languages = mapOf(
                LanguageName.NORWEGIAN to LanguageProficiencyLevel.B2,
                LanguageName.ENGLISH to LanguageProficiencyLevel.C2
            )
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/ai/suggest-vocabulary - suggest vocabulary")
    inner class SuggestVocabulary {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should generate vocabulary suggestions without context`() {
                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = null,
                    excludedWords = null
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.suggestions.shouldNotBeEmpty()
                response.suggestions.size shouldBeGreaterThanOrEqualTo 10

                response.suggestions.forEach { suggestion ->
                    suggestion.word.shouldNotBeBlank()
                    suggestion.translation.shouldNotBeBlank()
                    suggestion.definition.shouldNotBeBlank()
                }

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_SUGGEST_VOCABULARY")
            }

            @Test
            fun `200 - should generate vocabulary suggestions with context`() {
                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = "for business meetings and professional emails",
                    excludedWords = null
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.suggestions.shouldNotBeEmpty()
                response.suggestions.size shouldBe 10

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_SUGGEST_VOCABULARY")
            }

            @Test
            fun `200 - should exclude specified words from suggestions`() {
                val excludedWords = listOf("hund", "katt", "hus", "bil", "mat")

                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = null,
                    excludedWords = excludedWords
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.suggestions.shouldNotBeEmpty()

                // Verify none of the excluded words appear in suggestions
                val suggestedWords = response.suggestions.map { it.word.lowercase() }
                excludedWords.forEach { excludedWord ->
                    assert(!suggestedWords.contains(excludedWord.lowercase())) {
                        "Excluded word '$excludedWord' should not appear in suggestions"
                    }
                }
            }

            @Test
            fun `200 - should handle empty excludedWords list`() {
                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = null,
                    excludedWords = emptyList()
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.suggestions.shouldNotBeEmpty()
                response.suggestions.size shouldBeGreaterThanOrEqualTo 10
            }

            @Test
            fun `200 - should generate different suggestions with excludedWords across multiple requests`() {
                // First request without exclusions
                val firstRequest = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = "daily conversation",
                    excludedWords = null
                )

                val firstResponse = wordAIAPIClient.suggestVocabulary(
                    body = firstRequest,
                    user = authenticatedUser
                )

                firstResponse.status shouldBe HttpStatus.OK
                firstResponse.suggestions.shouldNotBeEmpty()

                // Collect words from first response to exclude in second request
                val wordsToExclude = firstResponse.suggestions.map { it.word }

                // Second request excluding words from first response
                val secondRequest = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = "daily conversation",
                    excludedWords = wordsToExclude
                )

                val secondResponse = wordAIAPIClient.suggestVocabulary(
                    body = secondRequest,
                    user = authenticatedUser
                )

                secondResponse.status shouldBe HttpStatus.OK
                secondResponse.suggestions.shouldNotBeEmpty()

                // Verify second response doesn't contain any words from first response
                val secondSuggestedWords = secondResponse.suggestions.map { it.word.lowercase() }
                wordsToExclude.forEach { excludedWord ->
                    assert(!secondSuggestedWords.contains(excludedWord.lowercase())) {
                        "Word '$excludedWord' from first request should not appear in second response"
                    }
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot get vocabulary suggestions`() {
                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = null,
                    excludedWords = null
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - context exceeding max length should fail`() {
                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = "a".repeat(501),
                    excludedWords = null
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - excludedWords exceeding max size should fail`() {
                val tooManyWords = (1..1001).map { "word$it" }

                val request = com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest(
                    language = LanguageName.NORWEGIAN,
                    context = null,
                    excludedWords = tooManyWords
                )

                val response = wordAIAPIClient.suggestVocabulary(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/ai/generate-manual - generate word manual")
    inner class GenerateWordManual {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should generate manual for a simple word`() {
                val request = GenerateWordManualRequest(
                    word = "hund",
                    language = LanguageName.NORWEGIAN,
                    targetLanguage = LanguageName.ENGLISH,
                    proficiencyLevel = LanguageProficiencyLevel.B2
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                val manual = response.body!!
                manual.suggestedCorrection shouldBe null // Word is spelled correctly
                manual.translation.shouldNotBeBlank()
                manual.definition.shouldNotBeBlank()
                manual.type shouldNotBe null
                manual.exampleSentences.shouldNotBeEmpty()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_GENERATE_MANUAL")
            }

            @Test
            fun `200 - should generate manual without target language`() {
                val request = GenerateWordManualRequest(
                    word = "hygge",
                    language = LanguageName.NORWEGIAN,
                    targetLanguage = null,
                    proficiencyLevel = LanguageProficiencyLevel.B1
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
            }

            @Test
            fun `200 - should generate manual without proficiency level`() {
                val request = GenerateWordManualRequest(
                    word = "katt",
                    language = LanguageName.NORWEGIAN,
                    targetLanguage = LanguageName.ENGLISH,
                    proficiencyLevel = null
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
            }

            @Test
            fun `200 - should generate manual for phrase`() {
                val request = GenerateWordManualRequest(
                    word = "break the ice",
                    language = LanguageName.ENGLISH,
                    targetLanguage = LanguageName.NORWEGIAN,
                    proficiencyLevel = LanguageProficiencyLevel.C1
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
            }

            @Test
            fun `200 - should return suggestedCorrection when word is misspelled`() {
                val request = GenerateWordManualRequest(
                    word = "recieve", // Misspelled "receive"
                    language = LanguageName.ENGLISH,
                    targetLanguage = LanguageName.NORWEGIAN,
                    proficiencyLevel = LanguageProficiencyLevel.B2
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                val manual = response.body!!

                // Should have suggested correction
                manual.suggestedCorrection shouldNotBe null
                manual.suggestedCorrection shouldBe "receive"

                // Manual should be generated for the corrected word
                manual.originalWord shouldBe "recieve"
                manual.translation.shouldNotBeBlank()
                manual.definition.shouldNotBeBlank()
                manual.exampleSentences.shouldNotBeEmpty()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "WORDS_GENERATE_MANUAL")
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot generate manual`() {
                val request = GenerateWordManualRequest(
                    word = "hund",
                    language = LanguageName.NORWEGIAN,
                    targetLanguage = LanguageName.ENGLISH,
                    proficiencyLevel = LanguageProficiencyLevel.B2
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - empty word should fail`() {
                val request = GenerateWordManualRequest(
                    word = "",
                    language = LanguageName.NORWEGIAN,
                    targetLanguage = LanguageName.ENGLISH,
                    proficiencyLevel = LanguageProficiencyLevel.B2
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - extremely long word should fail`() {
                val request = GenerateWordManualRequest(
                    word = "a".repeat(256),
                    language = LanguageName.NORWEGIAN,
                    targetLanguage = LanguageName.ENGLISH,
                    proficiencyLevel = LanguageProficiencyLevel.B2
                )

                val response = wordAIAPIClient.generateManual(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}
