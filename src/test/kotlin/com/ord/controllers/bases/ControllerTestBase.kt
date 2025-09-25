package com.ord.controllers.bases

/*

import com.ord.config.properties.JwtProperties
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserMapper
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.mocks.games.GameMockerBase
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
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
    val objectMapper: ObjectMapper,
    val mockMvc: MockMvc,
    val jwtProperties: JwtProperties,
    val languageProficiencyRepository: LanguageProficiencyRepository,
    val userMapper: UserMapper,
    val userRepository: UserRepository
): TestcontainersConfig() {
    val faker = Faker()

    companion object {
        inline fun <reified T> getResponseBody(objectMapper: ObjectMapper, source: Any): T {
            val content = extractContent(source)

            val result = objectMapper.readValue(content, object : TypeReference<T>() {})

            assert(result != null) { "Failed to parse response body! The response body is empty" }
            assert(result is T) { "Failed to parse response body! The response body is not of the expected type" }

            return result
        }

        fun <T> getResponseBody(
            objectMapper: ObjectMapper,
            source: Any,
            typeReference: TypeReference<T>
        ): T {
            val content = extractContent(source)

            val result = objectMapper.readValue(content, typeReference)

            requireNotNull(result) { "Failed to parse response body! The response body is empty" }

            return result
        }

        fun extractContent(source: Any): String {
            return when (source) {
                is MvcResult -> source.response.contentAsString
                is MockHttpServletResponse -> source.contentAsString
                else -> throw IllegalArgumentException("Unsupported source type: ${source::class.simpleName}")
            }
        }

    }

    fun mockAuthenticatedUser(
        email: String = faker.internet().emailAddress(),
        nativeLanguage: LanguageName = LanguageName.ENGLISH,
        languages: Map<LanguageName, LanguageProficiencyLevel> = mapOf(
            GameMockerBase.Companion.DefaultParams.language to LanguageProficiencyLevel.C1
        ),
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
                LanguageProficiencyEntity(
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

    final inline fun <reified T> getResponseBody(source: Any): T {
        return getResponseBody(objectMapper, source)
    }

    fun <T> getResponseBody(source: Any, typeReference: TypeReference<T>): T {
        return getResponseBody(objectMapper, source, typeReference)
    }
}

 */