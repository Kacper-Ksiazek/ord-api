package com.ord.testing_utils.api.clients

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class AuthAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/auth"

    fun register(
        body: RegisterRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<UserDTO?> {
        return post(
            url = "$baseUrl/register",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<UserDTO>() {}
        )
    }


    fun login(
        body: LoginRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<UserDTO?> {
        return post(
            url = "$baseUrl/login",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<UserDTO>() {}
        )
    }


    fun me(
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<UserDTO?> {
        return get(
            url = "$baseUrl/me",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<UserDTO>() {}
        )
    }


    fun logout(
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit?> {
        return delete<Unit>(
            url = "$baseUrl/logout",
            user = user,
        )
    }
}