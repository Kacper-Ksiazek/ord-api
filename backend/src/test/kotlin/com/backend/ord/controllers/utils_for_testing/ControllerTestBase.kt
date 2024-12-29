package com.backend.ord.controllers.utils_for_testing

import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.language.LanguageName
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertNotNull

@AutoConfigureMockMvc
abstract class ControllerTestBase(
    protected val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    private val jwtProperties: JwtProperties
) {
    fun mockAuthenticatedUser(): MockedAuthenticatedUser {
        val authUserEmailAddress = "random.authenticated.email@gmail.com"
        return mockAuthenticatedUser(authUserEmailAddress)
    }

    fun mockAuthenticatedUser(email: String): MockedAuthenticatedUser {
        // Create a request
        val request = MockMvcRequestBuilders.post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    RegisterRequest(
                        name = "Test User",
                        email = email,
                        password = "qwerty123",
                        nativeLanguage = LanguageName.ENGLISH
                    )
                )
            )

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated
        ).andReturn().response

        // Parse the response
        val authCookie = response.getCookie(jwtProperties.authCookieName).also {
            assertNotNull(it) { "Failed to get the auth cookie from the response" }
        }!!

        val userInfo: UserDTO = getResponseBody(response)

        return MockedAuthenticatedUser(
            token = authCookie.value,
            userInfo = userInfo,
            authCookie = authCookie,
            email = email
        )
    }

    inline fun <reified T> getResponseBody(source: Any): T {
        val content = when (source) {
            is MvcResult -> source.response.contentAsString
            is MockHttpServletResponse -> source.contentAsString
            else -> throw IllegalArgumentException("Unsupported source type")
        }

        val result = objectMapper.readValue(content, object : TypeReference<T>() {});

        assert(result != null) { "Failed to parse response body! The response body is empty" }
        assert(result is T) { "Failed to parse response body! The response body is not of the expected type" }

        return result;
    }
}
