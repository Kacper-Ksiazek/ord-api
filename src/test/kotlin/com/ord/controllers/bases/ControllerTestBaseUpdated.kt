package com.ord.controllers.bases

import com.github.javafaker.Faker
import com.ord.config.properties.JwtProperties
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import com.ord.testing_utils.mocks.games.GameMockerBase
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.test.web.reactive.server.WebTestClient

abstract class ControllerTestBaseUpdated(
    val webClient: WebTestClient,
    val jwtProperties: JwtProperties
) : TestcontainersConfig() {
    val faker = Faker()

    fun mockAuthenticatedUser(
        email: String = faker.internet().emailAddress(),
        nativeLanguage: LanguageName = LanguageName.ENGLISH,
        languages: Map<LanguageName, LanguageProficiencyLevel> = mapOf(
            GameMockerBase.Companion.DefaultParams.language to LanguageProficiencyLevel.C1
        )
    ): MockedAuthenticatedUserUpdated {
        val response = webClient
            .post()
            .uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                RegisterRequest(
                    name = "Test User",
                    email = email,
                    password = "qwerty123",
                    nativeLanguage = nativeLanguage
                )
            )
            .exchange()
            .expectBody(UserDTO::class.java)
            .returnResult()

        val authCookie: ResponseCookie = response.responseCookies[jwtProperties.authCookieName]?.firstOrNull() ?: run {
            throw IllegalStateException("Failed to get the auth cookie from the response")
        }

        return MockedAuthenticatedUserUpdated(
            token = authCookie.value,
            userInfo = response.responseBody!!,
            authCookie = authCookie,
            email = email
        )
    }
}