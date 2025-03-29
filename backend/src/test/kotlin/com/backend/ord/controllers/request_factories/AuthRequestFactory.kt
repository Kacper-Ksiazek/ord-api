package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.LoginRequest
import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.enums.persistence.language.LanguageName
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class AuthRequestFactory(
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
                    password = PASSWORD,
                    nativeLanguage = LanguageName.POLISH
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