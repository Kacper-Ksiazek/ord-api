package com.backend.ord.controllers.utils

import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.domain.dto.UserDTO
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.UnsupportedEncodingException

@AutoConfigureMockMvc
abstract class ControllerTestBase(
    protected val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val jwtProperties: JwtProperties
) {
    @Throws(Exception::class)
    fun mockedAuthenticatedUser(): MockedAuthenticatedUser {
        val authUserEmailAddress = "random.authenticated.email@gmail.com"
        return mockedAuthenticatedUser(authUserEmailAddress)
    }

    @Throws(Exception::class)
    fun mockedAuthenticatedUser(email: String?): MockedAuthenticatedUser {
        // Create a request
        val request = MockMvcRequestBuilders.post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    RegisterRequest(
                        name = "Test User",
                        email = email,
                        password = "qwerty123"
                    )
                )
            )

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated
        ).andReturn().response

        // Parse the response
        val authCookie = response.getCookie(jwtProperties.authCookieName)!!
        val userInfo: UserDTO = objectMapper.readValue(response.contentAsString, UserDTO::class.java)

        return MockedAuthenticatedUser(
            token = authCookie.value,
            userInfo = userInfo,
            authCookie = authCookie,
            email = email
        )
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    fun <T> getResponseBody(result: MvcResult): T {
        return objectMapper.readValue(result.response.contentAsString, object : TypeReference<T>() {})
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    fun <T> getResponseBody(response: MockHttpServletResponse): T {
        return objectMapper.readValue(response.contentAsString, object : TypeReference<T>() {})
    }
}
