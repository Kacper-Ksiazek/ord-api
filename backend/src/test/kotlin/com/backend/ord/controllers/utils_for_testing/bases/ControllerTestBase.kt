package com.backend.ord.controllers.utils_for_testing.bases

import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertNotNull

@AutoConfigureMockMvc
abstract class ControllerTestBase @Autowired constructor(
    val objectMapper: ObjectMapper,
    protected val mockMvc: MockMvc,

    private val jwtProperties: JwtProperties,
    private val userMapper: UserMapper,
    private val languageProficiencyRepository: LanguageProficiencyRepository
) {
    val faker = Faker()

    fun mockAuthenticatedUser(
        email: String = faker.internet().emailAddress(),
        nativeLanguage: LanguageName = LanguageName.ENGLISH,
        languages: Map<LanguageName, LanguageProficiencyLevel> = mapOf(),
    ): MockedAuthenticatedUser {
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
                        nativeLanguage = nativeLanguage
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

        // Integrate over the languages and save them as language proficiencies associated with the user
        languageProficiencyRepository.saveAll(
            languages.map { (language, proficiency) ->
                LanguageProficiency(
                    language = language,
                    proficiency = proficiency,
                    generativeContentLanguage = language,
                    user = userMapper.toEntity(userInfo),
                )
            }
        )

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