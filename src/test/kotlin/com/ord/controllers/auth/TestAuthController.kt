package com.ord.controllers.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBaseUpdated
import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepositoryReactive
import com.ord.core.security.UserSessionRepositoryReactive
import com.ord.core.user.model.UserDTO
import com.ord.seeders.entities.UserSeeder
import com.ord.testing_utils.api.clients.AuthAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
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
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- AuthController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestAuthController @Autowired constructor(
    private val userSeeder: UserSeeder,
    private val userRepository: UserRepositoryReactive,
    private val userSessionRepository: UserSessionRepositoryReactive,
    webClient: WebTestClient,

    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties,
) : ControllerTestBaseUpdated(
    webClient,
    jwtProperties = jwtProperties,
) {
    private val authAPIClient = AuthAPIClient(webClient)

    object TestData {
        const val USERNAME: String = "John Doe"
        const val PASSWORD: String = "123456"
        const val EMAIL: String = "test@test.com"

        object APIRequestPayloads {
            val register: RegisterRequest = RegisterRequest(
                nativeLanguage = LanguageName.POLISH,
                name = USERNAME,
                email = EMAIL,
                password = PASSWORD,
            )

            val login: LoginRequest = LoginRequest(
                email = EMAIL,
                password = PASSWORD,
            )
        }
    }

    @AfterEach
    fun cleanup() {
        userRepository.deleteByEmail(TestData.EMAIL).block()
    }

    @Nested
    @DisplayName("[POST] /api/v1/auth/register - register a new user account")
    inner class RegisterTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var response: APIClientResponse<UserDTO>

            @BeforeEach
            fun beforeEach() {
                userRepository.findByEmail(TestData.EMAIL).block() shouldBe null

                val rawResponse = authAPIClient.register(TestData.APIRequestPayloads.register)

                rawResponse.body shouldNotBe null

                @Suppress("Unchecked_cast")
                response = rawResponse as APIClientResponse<UserDTO>
            }

            @Test
            fun `201 - user with given email should be created`() {
                userRepository.findByEmail(TestData.EMAIL).block() shouldNotBe null

                response.body shouldNotBe null
                response.body?.email shouldBe TestData.EMAIL

            }

            @Test
            fun `201 - after successful register a new session should be created`() {
                assertSessionHasBeenCreated(response)
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `400 - with an existing email should return 400`() {
                // First, create a user
                userSeeder.insertRowWithCredentials(
                    email = TestData.EMAIL,
                    password = TestData.PASSWORD,
                )

                val response = authAPIClient.register(TestData.APIRequestPayloads.register)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `403 - register route should be available only for anonymous users`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.register(
                    user = authenticatedUser,
                    body = TestData.APIRequestPayloads.register,
                )

                response.status shouldBe HttpStatus.FORBIDDEN
            }
        }
    }


    @Nested
    @DisplayName("[POST] /api/v1/auth/login - login a user")
    inner class LoginTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            lateinit var response: APIClientResponse<UserDTO>

            @BeforeEach
            fun beforeEach() {
                userSeeder.insertRowWithCredentials(TestData.EMAIL, TestData.PASSWORD)

                val rawResponse = authAPIClient.login(TestData.APIRequestPayloads.login)

                rawResponse.body shouldNotBe null

                @Suppress("Unchecked_cast")
                response = rawResponse as APIClientResponse<UserDTO>
            }

            @Test
            fun `200 - User can login with correct credentials`() {
                response.status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - after successful login a new session should be created`() {
                assertSessionHasBeenCreated(response)
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - login route should be available only for anonymous users`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.login(
                    body = TestData.APIRequestPayloads.login,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.FORBIDDEN
            }

            @Test
            fun `404 - login with non-existing email should return 404`() {
                val response = authAPIClient.login(
                    LoginRequest(
                        email = faker.internet().emailAddress(),
                        password = TestData.PASSWORD,
                    )
                )

                response.status shouldBe HttpStatus.NOT_FOUND
                response.body shouldBe null
            }

            @Test
            fun `404 - user must not be able to authenticate with invalid password`() {
                userSeeder.insertRowWithCredentials(TestData.EMAIL, TestData.PASSWORD)

                val response = authAPIClient.login(
                    LoginRequest(
                        email = TestData.EMAIL,
                        password = "qwertyuiop"
                    )
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }


    @Nested
    @DisplayName("[DELETE] /api/v1/auth/logout - logout a user")
    inner class LogoutTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - User can logout`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.logout(
                    user = authenticatedUser
                )

                userSessionRepository.findByToken(authenticatedUser.token).block() shouldBe null

                response
                    .cookies[jwtProperties.authCookieName]
                    ?.firstOrNull()
                    .let {
                        it shouldNotBe null
                        it!!
                    }
            }
        }


        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - logout route should be available only for authenticated users`() {
                val request = authAPIClient.logout()

                request.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/auth/me - get information about the authenticated user")
    inner class MeTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - me endpoint should return information about the authenticated user`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.me(
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.email shouldBe authenticatedUser.userInfo.email
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - me endpoint should return 401 for anonymous users`() {
                val response = authAPIClient.me()

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

        }
    }


    // ------------------------------
    // Helper methods
    // ------------------------------

    private fun assertSessionHasBeenCreated(response: APIClientResponse<UserDTO>) {
        val token: String = response
            .cookies[jwtProperties.authCookieName]
            ?.firstOrNull()
            .let {
                it shouldNotBe null
                it!!.value
            }

        val session = userSessionRepository
            .findByToken(token)
            .block()

        session shouldNotBe null
        session!!.token shouldBe token

        // Verify user exists with the session
        val user = userRepository.findById(session.userId).block()
        user shouldNotBe null
        user!!.email shouldBe TestData.EMAIL
    }
}