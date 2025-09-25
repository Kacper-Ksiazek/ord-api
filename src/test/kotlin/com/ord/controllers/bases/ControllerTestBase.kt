package com.ord.controllers.bases

import com.github.javafaker.Faker
import com.ord.config.properties.JwtProperties
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.test.web.reactive.server.WebTestClient

abstract class ControllerTestBase(
    val webClient: WebTestClient,
    val jwtProperties: JwtProperties,
    val languageProficiencyRepository: LanguageProficiencyRepository,
) : TestcontainersConfig() {
    val faker = Faker()

    fun mockAuthenticatedUser(
        email: String = faker.internet().emailAddress(),
        nativeLanguage: LanguageName = LanguageName.ENGLISH,
        languages: Map<LanguageName, LanguageProficiencyLevel> = mapOf(
//            GameMockerBase.Companion.DefaultParams.language to LanguageProficiencyLevel.C1
            LanguageName.ENGLISH to LanguageProficiencyLevel.C1
        )
    ): MockedAuthenticatedUser {
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

        val userId = response.responseBody!!.id

        languageProficiencyRepository
            .saveAll(
                languages.map { (language, level) ->
                    LanguageProficiencyEntity(
                        userId = userId,
                        language = language,
                        level = level,
                        generativeContentLanguage = language,
                    )
                }
            )
            .collectList()
            .block()

        return MockedAuthenticatedUser(
            token = authCookie.value,
            userInfo = response.responseBody!!,
            authCookie = authCookie,
            email = email
        )
    }
}