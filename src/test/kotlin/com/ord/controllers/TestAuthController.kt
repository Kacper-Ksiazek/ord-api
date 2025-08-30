package com.ord.controllers

import com.ord.config.properties.JwtProperties
import com.ord.core.auth.services.UserSessionService
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserDTO
import com.ord.core.user.service.UserService
import com.ord.seeders.entities.UserSeeder
import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.controllers.bases.ControllerTestBaseUpdated
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.langugae_proficiency.model.enums.LanguageName
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
import org.springframework.http.HttpStatusCode
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- AuthController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestAuthController @Autowired constructor(
    private val userSessionService: UserSessionService,
    private val userService: UserService,
    private val userSeeder: UserSeeder,
    private val userRepository: UserRepository,
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
    }

    @AfterEach
    fun cleanup() {
        userRepository.deleteByEmail(TestData.EMAIL)
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
                userService.findUserByEmail(TestData.EMAIL) shouldBe null

                val rawResponse = authAPIClient.register(
                    RegisterRequest(
                        nativeLanguage = LanguageName.POLISH,
                        name = TestData.USERNAME,
                        email = TestData.EMAIL,
                        password = TestData.PASSWORD,
                    )
                )

                rawResponse.body shouldNotBe null

                @Suppress("Unchecked_cast")
                response = rawResponse as APIClientResponse<UserDTO>
            }

            @Test
            fun `201 - user with given email should be created`() {
                userService.findUserByEmail(TestData.EMAIL) shouldNotBe null

                response.body shouldNotBe null
                response.body?.email shouldBe TestData.EMAIL

            }

            @Test
            fun `201 - after successful singing in, a new session should be created`() {
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

                val response = authAPIClient.register(
                    RegisterRequest(
                        nativeLanguage = LanguageName.POLISH,
                        name = TestData.USERNAME,
                        email = TestData.EMAIL,
                        password = TestData.PASSWORD,
                    )
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `403 - register route should be available only for anonymous users`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.register(
                    user = authenticatedUser,
                    body = RegisterRequest(
                        nativeLanguage = LanguageName.POLISH,
                        name = TestData.USERNAME,
                        email = TestData.EMAIL,
                        password = TestData.PASSWORD,
                    )
                )

                response.status shouldBe HttpStatus.FORBIDDEN
            }
        }
    }
    /*


// ------------------------------
// /login
// ------------------------------

@Nested
@DisplayName("[POST] /api/v1/auth/login - login a user")
inner class LoginTests {

@Nested
@DisplayName("Positive")
inner class Positive {
    @Test
    fun `200 - User can login with correct credentials`() {
        // First, create a user
        userSeeder.insertRowWithCredentials(EMAIL, PASSWORD)

        // Create a request
        val request = authRequestFactory.loginRequest()

        // Perform the request
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        // Perform all kinds of needed assertions
        validateLoginOrRegisterResponse(response)
    }
}

@Nested
@DisplayName("Negative")
inner class Negative {
    @Test
    fun `403 - login route should be available only for anonymous users`() {
        val authenticatedUser = mockAuthenticatedUser()

        // Create a request
        val request = authRequestFactory.loginRequest(authenticatedUser)

        // Perform the request
        mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.FORBIDDEN.value()
        }
    }

    @Test
    fun `404 - login with non-existing email should return 404`() {
        // Create a request
        val request = authRequestFactory.loginRequest()

        // Perform the request
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.NOT_FOUND.value()
            it.response
        }

        // Assert the response body is empty
        validateResponseBodyIsEmpty(response)
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
        // First, generate an authenticated user
        val authenticatedUser = mockAuthenticatedUser()

        // Assert a session is created
        assertUserSessionHasBeenCreated(authenticatedUser.token, authenticatedUser.email)

        // Prepare a request to /logout
        val request = authRequestFactory.logoutRequest(authenticatedUser)

        // Perform the request
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        assertNull(userSessionService.findByToken(authenticatedUser.token))

        // Auth cookie should have no value
        val authCookie = response.getCookie(jwtProperties.authCookieName)!!
        assert(authCookie.value.isEmpty())
    }
}


@Nested
@DisplayName("Negative")
inner class Negative {
    @Test
    fun `403 - logout route should be available only for authenticated users`() {
        // Create a request
        val request = authRequestFactory.logoutRequest()

        // Perform the request
        mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.FORBIDDEN.value()
        }
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
        // First, create a user
        val authenticatedUser = mockAuthenticatedUser(EMAIL)

        // Create a request
        val request = authRequestFactory.meRequest(authenticatedUser)

        // Perform the request
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        // Assert the response data contains information about the user
        assertResponseContainsProperInformationAboutTheUser(response)
    }

}

@Nested
@DisplayName("Negative")
inner class Negative {
    @Test
    fun `403 - me endpoint should return 401 for anonymous users`() {
        // Create a request
        val request = authRequestFactory.meRequest()

        // Perform the request
        mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.FORBIDDEN.value()
        }
    }

}
}


*/

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

        userSessionService
            .findByToken(token)
            .also {
                it shouldNotBe null

                it!!.token shouldBe token
                it.user.email shouldBe TestData.EMAIL
            }!!
    }
}
