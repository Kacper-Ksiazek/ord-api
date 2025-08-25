package com.ord.testing_utils.api

import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.test.web.reactive.server.WebTestClient

interface APITestClient {
    fun get(
        url: String,
        queryParams: Map<String, String>? = null,
        user: MockedAuthenticatedUser?,
    ): WebTestClient.ResponseSpec


    fun <TBody> post(
        url: String,
        body: TBody? = null,
        user: MockedAuthenticatedUser?,
    ): WebTestClient.ResponseSpec


    fun <TBody> put(
        url: String,
        body: TBody? = null,
        user: MockedAuthenticatedUser?,
    ): WebTestClient.ResponseSpec


    fun delete(
        url: String,
        user: MockedAuthenticatedUser?,
    ): WebTestClient.ResponseSpec
}