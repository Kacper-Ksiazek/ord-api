package com.ord.testing_utils.api.clients.impl

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.clients.AuthAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.test.web.reactive.server.WebTestClient

class AuthAPIClientImpl(
    private val api: APITestClient
) : AuthAPIClient {
    val baseUrl = "/api/v1/auth"

    override fun register(
        body: RegisterRequest,
        user: MockedAuthenticatedUser?
    ): WebTestClient.ResponseSpec {
        return api.post("$baseUrl/register", body, user)
    }


    override fun login(
        body: LoginRequest,
        user: MockedAuthenticatedUser?
    ): WebTestClient.ResponseSpec {
        return api.post("$baseUrl/login", body, user)
    }


    override fun me(
        user: MockedAuthenticatedUser?
    ): WebTestClient.ResponseSpec {
        return api.get("$baseUrl/me", user)
    }


    override fun logout(
        user: MockedAuthenticatedUser?
    ): WebTestClient.ResponseSpec {
        return api.delete("$baseUrl/logout", user)
    }
}