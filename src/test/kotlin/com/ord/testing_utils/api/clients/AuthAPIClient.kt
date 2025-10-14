package com.ord.testing_utils.api.clients

import com.ord.core.auth.api.requests.dto.OtpRequestDto
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.user.model.UserDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class AuthAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/auth"

    fun requestOtp(
        body: OtpRequestDto,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return post(
            url = "$baseUrl/otp-request",
            body = body,
            user = user,
            responseBodyType = null
        )
    }

    fun verifyOtp(
        body: OtpVerifyDto,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<UserDTO?> {
        return post(
            url = "$baseUrl/otp-verify",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<UserDTO>() {}
        )
    }

    fun logout(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return delete(
            url = "$baseUrl/logout",
            user = user,
            responseBodyType = null
        )
    }
}