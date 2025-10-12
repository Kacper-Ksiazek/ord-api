package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.word.api.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.responses.dto.AIGeneratedWordManual
import com.ord.testing_utils.api.clients.WordAIAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldNotBeEmpty
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
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
@DisplayName("- WordAIController")
class TestWordAIController @Autowired constructor(
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
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
                manual.translation.shouldNotBeBlank()
                manual.definition.shouldNotBeBlank()
                manual.type shouldNotBe null
                manual.exampleSentences.shouldNotBeEmpty()
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
