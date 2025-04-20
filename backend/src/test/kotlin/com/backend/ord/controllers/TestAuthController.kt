package com.backend.ord.controllers

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.bases.ControllerTestBase
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.entities.UserSession
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.repositories.UserRepository
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.services.UserService
import com.backend.ord.services.UserSessionService
import com.backend.ord.testing_utils.api_requests_factories.AuthRequestFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- AuthenticationController")
class TestAuthController @Autowired constructor(
    private val userSessionService: UserSessionService,
    private val userService: UserService,
    private val userSeeder: UserSeeder,

    objectMapper: ObjectMapper,
    mockMvc: MockMvc,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userMapper: UserMapper,
    userRepository: UserRepository
) : ControllerTestBase(
    objectMapper = objectMapper,
    mockMvc = mockMvc,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userMapper = userMapper,
    userRepository = userRepository
) {
    private val PASSWORD = "123456"
    private val EMAIL = "test@test.com"
    private val BASE_URL = "/api/v1/auth"

    private val authRequestFactory = AuthRequestFactory(PASSWORD, EMAIL, BASE_URL, objectMapper)

    @AfterEach
    fun cleanup() {
        userRepository.deleteByEmail(EMAIL)
    }

    @Nested
    @DisplayName("General")
    inner class GeneralAuthTests {
        @Test
        fun `401 - when trying to access restricted resource without providing the cookie token`() {
            // This method already ensures that cookie is not null
            val authenticatedUser = mockAuthenticatedUser()
            // Assert a session is created
            assertUserSessionHasBeenCreated(authenticatedUser.token, authenticatedUser.email)

            // Create a request to /me
            val request = authRequestFactory.meRequest()

            // Without providing the cookie token, /me should return 401
            mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
            )

            // But with the cookie token, it should return 200
            val requestWithCookie = request.cookie(authenticatedUser.authCookie)
            mockMvc.perform(requestWithCookie).andExpect(
                MockMvcResultMatchers.status().isOk()
            )
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/auth/register - register a new user account")
    inner class RegisterTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - new user can be registed`() {
                // Initially, there should be no user with the email in the database
                assertNull(userService.findUserByEmail(EMAIL))
                // Create a request
                val request = authRequestFactory.registerRequest()

                // Perform the request
                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
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
            fun `400 - with an existing email should return 400`() {
                // First, create a user
                userSeeder.insertRowWithCredentials(EMAIL, PASSWORD)

                // Create a request
                val request = authRequestFactory.registerRequest()

                // Perform the request
                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }

                // Assert the response body is empty
                validateResponseBodyIsEmpty(response)
            }

            @Test
            fun `403 - register route should be available only for anonymous users`() {
                val authenticatedUser = mockAuthenticatedUser()

                // Create a request
                val request = authRequestFactory.registerRequest(authenticatedUser)

                // Perform the request
                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }
        }
    }


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
            fun `401 - me endpoint should return 401 for anonymous users`() {
                // Create a request
                val request = authRequestFactory.meRequest()

                // Perform the request
                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
                }
            }

        }
    }


    // ------------------------------
    // Helper methods
    // ------------------------------

    private fun validateResponseBodyIsEmpty(response: MockHttpServletResponse) {
        // Assert the response body is empty
        assert(response.contentAsString.isEmpty())
    }

    private fun validateLoginOrRegisterResponse(response: MockHttpServletResponse) {
        // Assert a cookie is set
        val token = assertAuthCookieIsSet(response)

        // Assert a session is created
        assertUserSessionHasBeenCreated(token)

        // Assert the response data contains information about the user
        assertResponseContainsProperInformationAboutTheUser(response)
    }

    private fun assertResponseContainsProperInformationAboutTheUser(
        response: MockHttpServletResponse,
        expectedEmail: String = EMAIL
    ) {
        val data: UserDTO = getResponseBody(response)

        assert(data.email == expectedEmail)
    }

    private fun assertUserSessionHasBeenCreated(
        token: String,
        email: String = EMAIL
    ) {
        val correspondingSession: UserSession = userSessionService.findByToken(token).also {
            assertNotNull(it)
        }!!

        assert(correspondingSession.token == token)
        assert(correspondingSession.user.email == email)
    }

    private fun assertAuthCookieIsSet(response: MockHttpServletResponse): String {
        val cookie: Cookie = response.getCookie(jwtProperties.authCookieName).also {
            assertNotNull(it)
        }!!

        return cookie.value
    }
}
