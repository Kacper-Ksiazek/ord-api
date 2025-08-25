package com.ord.testing_utils.api.clients

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.test.web.reactive.server.WebTestClient

interface AuthAPIClient {
    fun register(
        body: RegisterRequest,
        user: MockedAuthenticatedUser? = null,
    ): WebTestClient.ResponseSpec


    fun login(
        body: LoginRequest,
        user: MockedAuthenticatedUser? = null,
    ): WebTestClient.ResponseSpec


    fun me(
        user: MockedAuthenticatedUser? = null,
    ): WebTestClient.ResponseSpec


    fun logout(
        user: MockedAuthenticatedUser? = null,
    ): WebTestClient.ResponseSpec
}