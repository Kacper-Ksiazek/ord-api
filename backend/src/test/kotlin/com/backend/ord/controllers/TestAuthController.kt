package com.backend.ord.controllers

import com.backend.ord.api.requests.LoginRequest
import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.utils_for_testing.ControllerTestBase
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.UserSession
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.services.UserService
import com.backend.ord.services.UserSessionService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

internal class AuthRequestFactory(
    private val PASSWORD: String,
    private val EMAIL: String,
    private val BASE_URL: String,
    private val objectMapper: ObjectMapper
) {
    /**
     * Create a request to /register
     */
    fun registerRequest(): MockHttpServletRequestBuilder = MockMvcRequestBuilders.post("$BASE_URL/register")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(
            objectMapper.writeValueAsString(
                RegisterRequest(
                    name = "Test User",
                    email = EMAIL,
                    password = PASSWORD
                )
            )
        )

    /**
     * Create an authenticated request to /register
     */
    fun registerRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder =
        this.registerRequest().cookie(authenticatedUser.authCookie)

    /**
     * Create a request to /login
     */
    fun loginRequest(): MockHttpServletRequestBuilder = MockMvcRequestBuilders.post("$BASE_URL/login")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(
            objectMapper.writeValueAsString(
                LoginRequest(EMAIL, PASSWORD)
            )
        )

    /**
     * Create an authenticated request to /login
     */
    fun loginRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder =
        this.loginRequest().cookie(authenticatedUser.authCookie)

    /**
     * Create a request to /logout
     */
    fun logoutRequest(): MockHttpServletRequestBuilder = MockMvcRequestBuilders.delete("$BASE_URL/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)

    /**
     * Create an authenticated request to /logout
     */
    fun logoutRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder =
        this.logoutRequest().cookie(authenticatedUser.authCookie)

    /**
     * Create a request to /me
     */
    fun meRequest(): MockHttpServletRequestBuilder = MockMvcRequestBuilders.get("$BASE_URL/me")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)

    /**
     * Create an authenticated request to /me
     */
    fun meRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder =
        this.meRequest().cookie(authenticatedUser.authCookie)
}

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TestAuthController @Autowired constructor(
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    private val jwtProperties: JwtProperties,
    private val userSessionService: UserSessionService,
    private val userService: UserService,
    private val userSeeder: UserSeeder
) : ControllerTestBase(mockMvc!!, objectMapper, jwtProperties) {
    private val PASSWORD = "123456"
    private val EMAIL = "test@test.com"
    private val BASE_URL = "/api/v1/auth"

    private val authRequestFactory = AuthRequestFactory(PASSWORD, EMAIL, BASE_URL, objectMapper)

    @Test
    fun `HTTP 401 when trying to access restricted resource without providing the cookie token`() {
        // This method already ensures that cookie is not null
        val authenticatedUser = this.mockedAuthenticatedUser()
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

    // ------------------------------
    // /register
    // ------------------------------

    @Test
    fun `Register - endpoint should return 201 and create a user`() {
        // Initially, there should be no user with the email in the database
        assertNull(userService.findUserByEmail(EMAIL))
        // Create a request
        val request = authRequestFactory.registerRequest()

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        // Perform all kinds of needed assertions
        validateLoginOrRegisterResponse(response)
    }

    @Test
    fun `Register - with an existing email should return 400`() {
        // First, create a user
        userSeeder.insertRowWithCredentials(EMAIL, PASSWORD)

        // Create a request
        val request = authRequestFactory.registerRequest()

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        ).andReturn().response

        // Assert the response body is empty
        validateResponseBodyIsEmpty(response)
    }

    @Test
    fun `Register - route should be available only for anonymous users`() {
        val authenticatedUser = this.mockedAuthenticatedUser()

        // Create a request
        val request = authRequestFactory.registerRequest(authenticatedUser)

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    // ------------------------------
    // /login
    // ------------------------------

    @Test
    fun `Login - endpoint should return 200 and create a session`() {
        // First, create a user
        userSeeder.insertRowWithCredentials(EMAIL, PASSWORD)

        // Create a request
        val request = authRequestFactory.loginRequest()

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andReturn().response

        // Perform all kinds of needed assertions
        validateLoginOrRegisterResponse(response)
    }

    @Test
    fun `Login - route should be available only for anonymous users`() {
        val authenticatedUser = this.mockedAuthenticatedUser()

        // Create a request
        val request = authRequestFactory.loginRequest(authenticatedUser)

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    @Test
    fun `Login - with non-existing email should return 404`() {
        // Create a request
        val request = authRequestFactory.loginRequest()

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        ).andReturn().response

        // Assert the response body is empty
        validateResponseBodyIsEmpty(response)
    }

    // ------------------------------
    // /logout
    // ------------------------------

    @Test
    @Transactional
    fun `Logout - endpoint should return 200 and delete the session`() {
        // First, generate an authenticated user
        val authenticatedUser = this.mockedAuthenticatedUser()

        // Assert a session is created
        assertUserSessionHasBeenCreated(authenticatedUser.token, authenticatedUser.email)

        // Prepare a request to /logout
        val request = authRequestFactory.logoutRequest(authenticatedUser)

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andReturn().response

        assertNull(userSessionService.findByToken(authenticatedUser.token))

        // Auth cookie should have no value
        val authCookie = response.getCookie(jwtProperties.authCookieName)!!
        assert(authCookie.value.isEmpty())
    }

    @Test
    fun `Logout - route should be available only for authenticated users`() {
        // Create a request
        val request = authRequestFactory.logoutRequest()

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    // ------------------------------
    // /me
    // ------------------------------

    @Test
    fun `Me - endpoint should return 200 and provide information about the user`() {
        // First, create a user
        val authenticatedUser = this.mockedAuthenticatedUser(EMAIL)

        // Create a request
        val request = authRequestFactory.meRequest(authenticatedUser)

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andReturn().response

        // Assert the response data contains information about the user
        assertResponseContainsProperInformationAboutTheUser(response)
    }

    @Test
    fun `Me - route should be available only for authenticated users`() {
        // Create a request
        val request = authRequestFactory.meRequest()

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isUnauthorized()
        ).andReturn()
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
