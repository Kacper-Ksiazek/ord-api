package com.ord.testing_utils.api.clients

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

class AuthAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/auth"

    fun register(
        body: RegisterRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<UserDTO> {
        val response = post("$baseUrl/register", body, user)
            .expectBody(UserDTO::class.java)
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders
        )
    }


    fun login(
        body: LoginRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<UserDTO> {
        val response = post("$baseUrl/login", body, user)
            .expectBody(UserDTO::class.java)
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders
        )
    }


    fun me(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<UserDTO> {
        val response = get("$baseUrl/me", user)
            .expectBody(UserDTO::class.java)
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders
        )
    }


    fun logout(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit> {
        val response = delete("$baseUrl/logout", user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders
        )
    }
}