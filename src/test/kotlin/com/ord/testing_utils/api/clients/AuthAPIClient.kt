package com.ord.testing_utils.api.clients

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.test.web.reactive.server.WebTestClient

class AuthAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/auth"

    fun register(
        body: RegisterRequest,
        user: MockedAuthenticatedUser? = null
    ): WebTestClient.ResponseSpec {
        return post("$baseUrl/register", body, user)
    }


    fun login(
        body: LoginRequest,
        user: MockedAuthenticatedUser? = null
    ): WebTestClient.ResponseSpec {
        return post("$baseUrl/login", body, user)
    }


    fun me(
        user: MockedAuthenticatedUser? = null
    ): WebTestClient.ResponseSpec {
        return get("$baseUrl/me", user)
    }


    fun logout(
        user: MockedAuthenticatedUser? = null
    ): WebTestClient.ResponseSpec {
        return delete("$baseUrl/logout", user)
    }
}