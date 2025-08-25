package com.ord.testing_utils.api.impl

import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class APITestClientImpl(
    private val webClient: WebTestClient
) : APITestClient {
    inline fun <reified T> parseBody(response: WebTestClient.ResponseSpec): T {
        return response.expectBody(T::class.java).returnResult().responseBody!!
    }


    override fun get(
        url: String,
        user: MockedAuthenticatedUser?,
        queryParams: Map<String, String>?,
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

                if (user != null) {
                    this.cookie(user.authCookie.name, user.authCookie.value)
                }
            }

        return request
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    }


    override fun <TBody> post(
        url: String,
        body: TBody?,
        user: MockedAuthenticatedUser?
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


    override fun <TBody> put(
        url: String,
        body: TBody?,
        user: MockedAuthenticatedUser?
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


    override fun delete(
        url: String,
        user: MockedAuthenticatedUser?
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
        user: MockedAuthenticatedUser?,
    ): WebTestClient.RequestHeadersSpec<*> {
        return if (user != null) {
            this.cookie(user.authCookie.name, user.authCookie.value)
        } else {
            this
        }
    }
}