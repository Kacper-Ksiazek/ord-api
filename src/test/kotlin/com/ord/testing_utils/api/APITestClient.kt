package com.ord.testing_utils.api

import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

abstract class APITestClient(
    val webClient: WebTestClient
) {
    fun get(
        url: String,
        user: MockedAuthenticatedUserUpdated?,
        queryParams: Map<String, String>? = null,
    ): WebTestClient.ResponseSpec {
        val request = webClient
            .get()
            .uri { uriBuilder ->
                var builder = uriBuilder.path(url)
                queryParams?.forEach { (key, value) ->
                    builder = builder.queryParam(key, value)
                }
                builder.build()
            }
            .apply {
                withAuth(user)
            }

        return request
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    }


    fun <TBody> post(
        url: String,
        body: TBody?,
        user: MockedAuthenticatedUserUpdated?
    ): WebTestClient.ResponseSpec {
        return webClient
            .post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .apply {
                withAuth(user)

                if (body != null) {
                    this.bodyValue(body)
                }
            }
            .exchange()
    }


    fun <TBody> put(
        url: String,
        body: TBody?,
        user: MockedAuthenticatedUserUpdated?
    ): WebTestClient.ResponseSpec {
        return webClient
            .put()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .apply {
                withAuth(user)

                if (body != null) {
                    this.bodyValue(body)
                }
            }
            .exchange()
    }


    fun delete(
        url: String,
        user: MockedAuthenticatedUserUpdated?
    ): WebTestClient.ResponseSpec {
        return webClient
            .delete()
            .uri(url)
            .apply {
                withAuth(user)
            }
            .exchange()
    }


    private fun WebTestClient.RequestHeadersSpec<*>.withAuth(
        user: MockedAuthenticatedUserUpdated?,
    ): WebTestClient.RequestHeadersSpec<*> {
        return if (user != null) {
            this.cookie(user.authCookie.name, user.authCookie.value)
        } else {
            this
        }
    }
}