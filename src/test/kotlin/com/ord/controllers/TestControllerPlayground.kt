package com.ord.controllers

import com.github.javafaker.Faker
import com.ord.controllers.bases.TestcontainersConfig
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.testing_utils.api.clients.AuthAPIClient
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserControllerTest(
    @Autowired val webClient: WebTestClient
) : TestcontainersConfig() {
    val faker = Faker()

    val authAPIClient = AuthAPIClient(webClient)

    @Test
    fun `should get user by id`() {
        val email: String = faker.internet().emailAddress()

        authAPIClient.register(
            RegisterRequest(
                name = "Test User",
                email = email,
                password = "qwerty123",
                nativeLanguage = LanguageName.ENGLISH
            )
        )
    }
}
