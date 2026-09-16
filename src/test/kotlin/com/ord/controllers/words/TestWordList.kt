package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.seeders.entities.WordSeeder
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

@DisplayName("- WordCRUDController: List")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestWordList @Autowired constructor(
    private val wordRepository: WordRepository,
    private val wordSeeder: WordSeeder,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository,
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository,
) {
    private val wordsAPIClient = WordsAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        wordRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[GET] /api/v1/words")
    inner class ListWords {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = wordsAPIClient.listWords(
                    language = LanguageName.POLISH,
                    user = null,
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should use default pagination`() {
                val user = mockAuthenticatedUser()
                wordSeeder.seedMultipleEntitiesForUser(
                    userId = user.userInfo.id,
                    amount = 60,
                    language = LanguageName.POLISH,
                )

                val response = wordsAPIClient.listWords(
                    language = LanguageName.POLISH,
                    user = user,
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.data.size shouldBe 50
                response.body.pagination.page shouldBe 0
                response.body.pagination.perPage shouldBe 50
                response.body.pagination.totalResults shouldBe 60
            }

            @Test
            fun `200 - should scope results to requested language`() {
                val user = mockAuthenticatedUser()

                wordSeeder.seedMultipleEntitiesForUser(
                    userId = user.userInfo.id,
                    amount = 2,
                    language = LanguageName.POLISH,
                )
                wordSeeder.seedMultipleEntitiesForUser(
                    userId = user.userInfo.id,
                    amount = 3,
                    language = LanguageName.ENGLISH,
                )

                val polishResponse = wordsAPIClient.listWords(
                    language = LanguageName.POLISH,
                    user = user,
                )
                val englishResponse = wordsAPIClient.listWords(
                    language = LanguageName.ENGLISH,
                    user = user,
                )

                polishResponse.status shouldBe HttpStatus.OK
                polishResponse.body!!.data.size shouldBe 2

                englishResponse.status shouldBe HttpStatus.OK
                englishResponse.body!!.data.size shouldBe 3
            }

            @Test
            fun `200 - should return only words with learning progress`() {
                val user = mockAuthenticatedUser()

                wordSeeder.seedMultipleEntitiesForUser(
                    userId = user.userInfo.id,
                    amount = 2,
                    language = LanguageName.POLISH,
                    withProgress = true,
                )
                wordSeeder.seedMultipleEntitiesForUser(
                    userId = user.userInfo.id,
                    amount = 1,
                    language = LanguageName.POLISH,
                    withProgress = false,
                )

                val response = wordsAPIClient.listWords(
                    language = LanguageName.POLISH,
                    user = user,
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.data.size shouldBe 2
            }

            @Test
            fun `200 - should only return words belonging to the authenticated user`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()

                wordSeeder.seedMultipleEntitiesForUser(
                    userId = userA.userInfo.id,
                    amount = 2,
                    language = LanguageName.POLISH,
                )
                wordSeeder.seedMultipleEntitiesForUser(
                    userId = userB.userInfo.id,
                    amount = 3,
                    language = LanguageName.POLISH,
                )

                val responseA = wordsAPIClient.listWords(
                    language = LanguageName.POLISH,
                    user = userA,
                )
                val responseB = wordsAPIClient.listWords(
                    language = LanguageName.POLISH,
                    user = userB,
                )

                responseA.status shouldBe HttpStatus.OK
                responseA.body!!.data.size shouldBe 2

                responseB.status shouldBe HttpStatus.OK
                responseB.body!!.data.size shouldBe 3
            }
        }
    }
}
