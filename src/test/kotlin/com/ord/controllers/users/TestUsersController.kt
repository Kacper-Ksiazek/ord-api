package com.ord.controllers.users

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.user.api.responses.MeResponse
import com.ord.testing_utils.api.clients.UsersAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
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

@DisplayName("- UsersController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestUsersController @Autowired constructor(
    private val userRepository: UserRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository
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
                response.body!!.name shouldBe authenticatedUser.userInfo.name
                response.body!!.nativeLanguage shouldBe authenticatedUser.userInfo.nativeLanguage
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
                val authenticatedUser = mockAuthenticatedUser()

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
}
