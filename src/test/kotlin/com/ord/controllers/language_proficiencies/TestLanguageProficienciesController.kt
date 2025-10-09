package com.ord.controllers.language_proficiencies

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.api.requests.CreateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.api.requests.UpdateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.testing_utils.api.clients.LanguageProficienciesAPIClient
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- LanguageProficienciesController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestLanguageProficienciesController @Autowired constructor(
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository
) {
    private val apiClient = LanguageProficienciesAPIClient(webClient)

    @Nested
    @DisplayName("[GET] /api/v1/language-proficiencies - get all language proficiencies for user")
    inner class GetLanguagesForUserTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return language proficiencies for authenticated user`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1,
                        LanguageName.SPANISH to LanguageProficiencyLevel.B2
                    )
                )

                val response = apiClient.getLanguagesForUser(user = authenticatedUser)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.size shouldBe 2
                response.body!! shouldContain (LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                response.body!! shouldContain (LanguageName.SPANISH to LanguageProficiencyLevel.B2)
            }

            @Test
            fun `200 - should return empty map when user has no language proficiencies`() {
                val authenticatedUser = mockAuthenticatedUser(languages = emptyMap())

                val response = apiClient.getLanguagesForUser(user = authenticatedUser)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.size shouldBe 0
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should return 401 for anonymous users`() {
                val response = apiClient.getLanguagesForUser()

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/language-proficiencies - create language proficiency")
    inner class CreateLanguageProficiencyTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create language proficiency for authenticated user`() {
                val authenticatedUser = mockAuthenticatedUser(languages = emptyMap())

                val request = CreateLanguageProficiencyRequest(
                    language = LanguageName.FRENCH,
                    level = LanguageProficiencyLevel.B1,
                    generativeContentLanguage = LanguageName.ENGLISH
                )

                val response = apiClient.createLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.language shouldBe LanguageName.FRENCH
                response.body!!.level shouldBe LanguageProficiencyLevel.B1
                response.body!!.generativeContentLanguage shouldBe LanguageName.ENGLISH
                response.body!!.userId shouldBe authenticatedUser.userInfo.id

                // Verify it was saved in database
                val saved = languageProficiencyRepository
                    .findUserProficiencyInLanguage(authenticatedUser.userInfo.id, LanguageName.FRENCH.name)
                    .block()

                saved shouldNotBe null
                saved!!.level shouldBe LanguageProficiencyLevel.B1
            }

            @Test
            fun `201 - should use language as generativeContentLanguage when not provided`() {
                val authenticatedUser = mockAuthenticatedUser(languages = emptyMap())

                val request = CreateLanguageProficiencyRequest(
                    language = LanguageName.GERMAN,
                    level = LanguageProficiencyLevel.A2,
                    generativeContentLanguage = null
                )

                val response = apiClient.createLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.generativeContentLanguage shouldBe LanguageName.GERMAN
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should return 401 for anonymous users`() {
                val request = CreateLanguageProficiencyRequest(
                    language = LanguageName.FRENCH,
                    level = LanguageProficiencyLevel.B1
                )

                val response = apiClient.createLanguageProficiency(body = request)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should fail when creating duplicate language proficiency`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ITALIAN to LanguageProficiencyLevel.A1)
                )

                val request = CreateLanguageProficiencyRequest(
                    language = LanguageName.ITALIAN,
                    level = LanguageProficiencyLevel.B2
                )

                val response = apiClient.createLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/language-proficiencies - update language proficiency")
    inner class UpdateLanguageProficiencyTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should update language proficiency level`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.SPANISH to LanguageProficiencyLevel.A1)
                )

                val request = UpdateLanguageProficiencyRequest(
                    language = LanguageName.SPANISH,
                    level = LanguageProficiencyLevel.B2
                )

                val response = apiClient.updateLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.language shouldBe LanguageName.SPANISH
                response.body!!.level shouldBe LanguageProficiencyLevel.B2

                // Verify in database
                val updated = languageProficiencyRepository
                    .findUserProficiencyInLanguage(authenticatedUser.userInfo.id, LanguageName.SPANISH.name)
                    .block()

                updated!!.level shouldBe LanguageProficiencyLevel.B2
            }

            @Test
            fun `200 - should update generativeContentLanguage`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ITALIAN to LanguageProficiencyLevel.A2)
                )

                val request = UpdateLanguageProficiencyRequest(
                    language = LanguageName.ITALIAN,
                    generativeContentLanguage = LanguageName.ENGLISH
                )

                val response = apiClient.updateLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.generativeContentLanguage shouldBe LanguageName.ENGLISH

                // Verify in database
                val updated = languageProficiencyRepository
                    .findUserProficiencyInLanguage(authenticatedUser.userInfo.id, LanguageName.ITALIAN.name)
                    .block()

                updated!!.generativeContentLanguage shouldBe LanguageName.ENGLISH
            }

            @Test
            fun `200 - should update both level and generativeContentLanguage`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ITALIAN to LanguageProficiencyLevel.A1)
                )

                val request = UpdateLanguageProficiencyRequest(
                    language = LanguageName.ITALIAN,
                    level = LanguageProficiencyLevel.C1,
                    generativeContentLanguage = LanguageName.POLISH
                )

                val response = apiClient.updateLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.level shouldBe LanguageProficiencyLevel.C1
                response.body!!.generativeContentLanguage shouldBe LanguageName.POLISH
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should return 401 for anonymous users`() {
                val request = UpdateLanguageProficiencyRequest(
                    language = LanguageName.SPANISH,
                    level = LanguageProficiencyLevel.B2
                )

                val response = apiClient.updateLanguageProficiency(body = request)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should fail when updating non-existent language proficiency`() {
                val authenticatedUser = mockAuthenticatedUser(languages = emptyMap())

                val request = UpdateLanguageProficiencyRequest(
                    language = LanguageName.RUSSIAN,
                    level = LanguageProficiencyLevel.B2
                )

                val response = apiClient.updateLanguageProficiency(
                    user = authenticatedUser,
                    body = request
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/language-proficiencies/{language} - delete language proficiency")
    inner class DeleteLanguageProficiencyTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should delete language proficiency`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ITALIAN to LanguageProficiencyLevel.A1,
                        LanguageName.NORWEGIAN to LanguageProficiencyLevel.B1
                    )
                )

                val response = apiClient.deleteLanguageProficiency(
                    user = authenticatedUser,
                    language = LanguageName.NORWEGIAN
                )

                response.status shouldBe HttpStatus.OK

                // Verify it was deleted from database
                val deleted = languageProficiencyRepository
                    .findUserProficiencyInLanguage(authenticatedUser.userInfo.id, LanguageName.NORWEGIAN.name)
                    .block()

                deleted shouldBe null

                // Verify other proficiency still exists
                val remaining = languageProficiencyRepository
                    .findUserProficiencyInLanguage(authenticatedUser.userInfo.id, LanguageName.ITALIAN.name)
                    .block()

                remaining shouldNotBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should return 401 for anonymous users`() {
                val response = apiClient.deleteLanguageProficiency(language = LanguageName.ITALIAN)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should fail when deleting non-existent language proficiency`() {
                val authenticatedUser = mockAuthenticatedUser(languages = emptyMap())

                val response = apiClient.deleteLanguageProficiency(
                    user = authenticatedUser,
                    language = LanguageName.ITALIAN
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}
