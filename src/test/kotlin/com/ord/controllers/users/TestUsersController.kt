package com.ord.controllers.users

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyCompactDTO
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.user.api.requests.InitUserAccountRequest
import com.ord.testing_utils.api.clients.UsersAPIClient
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- UsersController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestUsersController @Autowired constructor(
    userRepository: UserRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder
) {
    private val usersAPIClient = UsersAPIClient(webClient)

    @Nested
    @DisplayName("[GET] /api/v1/users/me - get information about the authenticated user")
    inner class MeTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - me endpoint should return information about the authenticated user`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = usersAPIClient.me(
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.email shouldBe authenticatedUser.email
                response.body.name shouldBe authenticatedUser.userInfo.name
                response.body.nativeLanguage shouldBe authenticatedUser.userInfo.nativeLanguage
                response.body.isAccountInitialized shouldBe true
            }

            @Test
            fun `200 - me endpoint should return selectedLearningLanguage when user has it set`() {
                val authenticatedUser = mockAuthenticatedUser(
                    nativeLanguage = LanguageName.POLISH
                )

                // Update user to have a selectedLearningLanguage
                val user = userRepository.findByEmail(authenticatedUser.email).block()!!
                user.selectedLearningLanguage = LanguageName.ENGLISH
                userRepository.save(user).block()

                val response = usersAPIClient.me(
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.selectedLearningLanguage shouldBe LanguageName.ENGLISH
            }

            @Test
            fun `200 - me endpoint should return null selectedLearningLanguage when user has not set it`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf()
                )

                val response = usersAPIClient.me(
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.selectedLearningLanguage shouldBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - me endpoint should return 401 for anonymous users`() {
                val response = usersAPIClient.me()

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/users/init-account - initialize user account")
    inner class InitUserAccountTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should successfully initialize user account with basic information`() {
                val authenticatedUser = mockAuthenticatedUserWithUninitializedAccount()

                val request = InitUserAccountRequest(
                    name = "John Doe",
                    nativeLanguage = LanguageName.POLISH,
                    selectedLearningLanguage = LanguageName.ENGLISH,
                    languageProficiencies = listOf(
                        LanguageProficiencyCompactDTO(
                            language = LanguageName.ENGLISH,
                            level = LanguageProficiencyLevel.B2,
                            generativeContentLanguage = LanguageName.POLISH
                        )
                    )
                )

                val response = usersAPIClient.initUserAccount(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
            }

            @Test
            fun `200 - should update user fields correctly in database`() {
                val authenticatedUser = mockAuthenticatedUserWithUninitializedAccount()

                val request = InitUserAccountRequest(
                    name = "Jane Smith",
                    nativeLanguage = LanguageName.ENGLISH,
                    selectedLearningLanguage = LanguageName.SPANISH,
                    languageProficiencies = emptyList()
                )

                val response = usersAPIClient.initUserAccount(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.name shouldBe "Jane Smith"
                response.body.nativeLanguage shouldBe LanguageName.ENGLISH
                response.body.selectedLearningLanguage shouldBe LanguageName.SPANISH
                response.body.isAccountInitialized shouldBe true
            }

            @Test
            fun `200 - should persist user changes in database`() {
                val authenticatedUser = mockAuthenticatedUserWithUninitializedAccount()

                val request = InitUserAccountRequest(
                    name = "Test User",
                    nativeLanguage = LanguageName.GERMAN,
                    selectedLearningLanguage = LanguageName.FRENCH,
                    languageProficiencies = emptyList()
                )

                usersAPIClient.initUserAccount(
                    body = request,
                    user = authenticatedUser
                )

                val user = userRepository.findByEmail(authenticatedUser.email).block()!!
                user.name shouldBe "Test User"
                user.nativeLanguage shouldBe LanguageName.GERMAN
                user.selectedLearningLanguage shouldBe LanguageName.FRENCH
                user.isAccountInitialized shouldBe true
            }

            @Test
            fun `200 - should save language proficiencies correctly`() {
                val authenticatedUser = mockAuthenticatedUserWithUninitializedAccount()

                val request = InitUserAccountRequest(
                    name = "Polyglot User",
                    nativeLanguage = LanguageName.POLISH,
                    selectedLearningLanguage = LanguageName.ENGLISH,
                    languageProficiencies = listOf(
                        LanguageProficiencyCompactDTO(
                            language = LanguageName.ENGLISH,
                            level = LanguageProficiencyLevel.C1,
                            generativeContentLanguage = LanguageName.POLISH
                        ),
                        LanguageProficiencyCompactDTO(
                            language = LanguageName.GERMAN,
                            level = LanguageProficiencyLevel.B1,
                            generativeContentLanguage = LanguageName.POLISH
                        )
                    )
                )

                usersAPIClient.initUserAccount(
                    body = request,
                    user = authenticatedUser
                )

                val user = userRepository.findByEmail(authenticatedUser.email).block()!!
                val proficiencies = languageProficiencyRepository.findAllByUserId(user.id!!).collectList().block()!!

                proficiencies shouldHaveSize 2
                proficiencies.find { it.language == LanguageName.ENGLISH }!!.level shouldBe LanguageProficiencyLevel.C1
                proficiencies.find { it.language == LanguageName.GERMAN }!!.level shouldBe LanguageProficiencyLevel.B1
            }

            @Test
            fun `200 - should handle empty language proficiencies list`() {
                val authenticatedUser = mockAuthenticatedUserWithUninitializedAccount()

                val request = InitUserAccountRequest(
                    name = "Minimal User",
                    nativeLanguage = LanguageName.ITALIAN,
                    selectedLearningLanguage = LanguageName.ENGLISH,
                    languageProficiencies = emptyList()
                )

                val response = usersAPIClient.initUserAccount(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                val user = userRepository.findByEmail(authenticatedUser.email).block()!!
                val proficiencies = languageProficiencyRepository.findAllByUserId(user.id!!).collectList().block()!!

                proficiencies shouldHaveSize 0
            }

            @Test
            fun `200 - should return updated user information in response`() {
                val authenticatedUser = mockAuthenticatedUserWithUninitializedAccount()

                val request = InitUserAccountRequest(
                    name = "Response Test",
                    nativeLanguage = LanguageName.POLISH,
                    selectedLearningLanguage = LanguageName.ENGLISH,
                    languageProficiencies = listOf(
                        LanguageProficiencyCompactDTO(
                            language = LanguageName.ENGLISH,
                            level = LanguageProficiencyLevel.A2,
                            generativeContentLanguage = LanguageName.POLISH
                        )
                    )
                )

                val response = usersAPIClient.initUserAccount(
                    body = request,
                    user = authenticatedUser
                )

                response.body!!.email shouldBe authenticatedUser.email
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should fail for anonymous users`() {
                val request = InitUserAccountRequest(
                    name = "Anonymous",
                    nativeLanguage = LanguageName.ENGLISH,
                    selectedLearningLanguage = LanguageName.SPANISH,
                    languageProficiencies = emptyList()
                )

                val response = usersAPIClient.initUserAccount(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }
}
