package com.backend.ord.controllers

import com.backend.ord.api.requests.LoginRequest
import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.utils.ControllerTestBase
import com.backend.ord.controllers.utils.MockedAuthenticatedUser
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.services.UserService
import com.backend.ord.services.UserSessionService
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
import java.io.UnsupportedEncodingException

internal class AuthRequestFactory(
    private val PASSWORD: String,
    private val EMAIL: String,
    private val BASE_URL: String,
    private val objectMapper: ObjectMapper
) {
    /**
     * Create a request to /register
     */
    @Throws(JsonProcessingException::class)
    fun registerRequest(): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post("$BASE_URL/register")
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
    }

    /**
     * Create an authenticated request to /register
     */
    @Throws(JsonProcessingException::class)
    fun registerRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder {
        return this.registerRequest().cookie(authenticatedUser.authCookie)
    }

    /**
     * Create a request to /login
     */
    @Throws(JsonProcessingException::class)
    fun loginRequest(): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post("$BASE_URL/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    LoginRequest(EMAIL, PASSWORD)
                )
            )
    }

    /**
     * Create an authenticated request to /login
     */
    @Throws(JsonProcessingException::class)
    fun loginRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder {
        return this.loginRequest().cookie(authenticatedUser.authCookie)
    }

    /**
     * Create a request to /logout
     */
    fun logoutRequest(): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.delete("$BASE_URL/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    }

    /**
     * Create an authenticated request to /logout
     */
    fun logoutRequest(authenticatedUser: MockedAuthenticatedUser?): MockHttpServletRequestBuilder {
        return this.logoutRequest().cookie(authenticatedUser!!.authCookie)
    }

    /**
     * Create a request to /me
     */
    fun meRequest(): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get("$BASE_URL/me")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    }

    /**
     * Create an authenticated request to /me
     */
    fun meRequest(authenticatedUser: MockedAuthenticatedUser): MockHttpServletRequestBuilder {
        return this.meRequest().cookie(authenticatedUser.authCookie)
    }
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
    @Throws(Exception::class)
    fun testControllerTestBaseAuthentication() {
        // This method already ensures that cookie is not null
        val authenticatedUser = this.mockedAuthenticatedUser()
        // Assert a session is created
        assertUserSessionHasBeenCreated(authenticatedUser.token, authenticatedUser.email)

        // Create a request to /me
        val request = authRequestFactory.meRequest()

        // Without providing the cookie token, /current-user-info should return 403
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        )

        // But with the cookie token, it should return 200
        val requestWithCookie = request.cookie(authenticatedUser.authCookie)
        mockMvc.perform(requestWithCookie).andExpect(
            MockMvcResultMatchers.status().isOk()
        )
    }

    @Test
    @Throws(Exception::class)
    fun testRegister() {
        // Initially, there should be no user with the email in the database
        assert(userService.findUserByEmail(EMAIL).isEmpty)
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
    @Throws(Exception::class)
    fun testLogin() {
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
    @Throws(Exception::class)
    fun testLogout() {
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

        assert(userSessionService.findByToken(authenticatedUser.token).isEmpty)
        // Auth cookie should have no value
        val authCookie = response.getCookie(jwtProperties.authCookieName)!!
        assert(authCookie.value.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testMe() {
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
    @Throws(Exception::class)
    fun testRegisterWithExistingEmailShouldReturn400() {
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
    @Throws(Exception::class)
    fun testRegisterRouteShouldBeAvailableOnlyForAnonymousUsers() {
        val authenticatedUser = this.mockedAuthenticatedUser()

        // Create a request
        val request = authRequestFactory.registerRequest(authenticatedUser)

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testLoginRouteShouldBeAvailableOnlyForAnonymousUsers() {
        val authenticatedUser = this.mockedAuthenticatedUser()

        // Create a request
        val request = authRequestFactory.loginRequest(authenticatedUser)

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testLoginWithNonExistingEmailShouldReturn400() {
        // Create a request
        val request = authRequestFactory.loginRequest()

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        ).andReturn().response

        // Assert the response body is empty
        validateResponseBodyIsEmpty(response)
    }

    @Test
    @Throws(Exception::class)
    fun testLogoutRouteShouldBeAvailableOnlyForAuthenticatedUsers() {
        // Create a request
        val request = authRequestFactory.logoutRequest()

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testMeRouteShouldBeAvailableOnlyForAuthenticatedUsers() {
        // Create a request
        val request = authRequestFactory.meRequest()

        // Perform the request
        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        ).andReturn()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun validateResponseBodyIsEmpty(response: MockHttpServletResponse) {
        // Assert the response body is empty
        assert(response.contentAsString.isEmpty())
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    private fun validateLoginOrRegisterResponse(response: MockHttpServletResponse) {
        // Assert a cookie is set
        val token = assertAuthCookieIsSet(response)

        // Assert a session is created
        assertUserSessionHasBeenCreated(token)

        // Assert the response data contains information about the user
        assertResponseContainsProperInformationAboutTheUser(response)
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    private fun assertResponseContainsProperInformationAboutTheUser(response: MockHttpServletResponse) {

        val data: UserDTO = getResponseBody<UserDTO>(response)
        assert(data.javaClass == UserDTO::class.java)
        assert(data.email == EMAIL)
    }

    private fun assertUserSessionHasBeenCreated(token: String?, email: String? = EMAIL) {
        val correspondingSession = userSessionService.findByToken(token)
        assert(correspondingSession.isPresent)
        assert(correspondingSession.get().token == token)
        assert(correspondingSession.get().user.email == email)
    }

    private fun assertAuthCookieIsSet(response: MockHttpServletResponse): String {
        val cookie = response.getCookie(jwtProperties.authCookieName)!!
        return cookie.value
    }
}
