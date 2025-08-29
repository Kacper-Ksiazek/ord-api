package com.ord.testing_utils.api.clients

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

class AuthAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/auth"

    fun register(
        body: RegisterRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<UserDTO> {
        return post("$baseUrl/register", body, user)
            .expectBody(UserDTO::class.java)
            .returnResult()
            .toApiClientResponse()
    }


    fun login(
        body: LoginRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<UserDTO> {
        return post("$baseUrl/login", body, user)
            .expectBody(UserDTO::class.java)
            .returnResult()
            .toApiClientResponse()
    }


    fun me(
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<UserDTO> {
        return get("$baseUrl/me", user)
            .expectBody(UserDTO::class.java)
            .returnResult()
            .toApiClientResponse()
    }


    fun logout(
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit> {
        val response = delete("$baseUrl/logout", user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    private fun EntityExchangeResult<UserDTO>.toApiClientResponse(): APIClientResponse<UserDTO> {
        return APIClientResponse<UserDTO>(
            body = responseBody,
            status = status,
            headers = responseHeaders,
            cookies = responseCookies
        )
    }
}