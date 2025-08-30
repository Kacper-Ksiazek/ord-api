package com.ord.testing_utils.api

import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.client.HttpStatusCodeException
import kotlin.reflect.KClass

abstract class APITestClient(
    val webClient: WebTestClient
) {
    fun <TBody> get(
        url: String,
        user: MockedAuthenticatedUserUpdated?,
        queryParams: Map<String, String>? = null,
        responseBodyClass: Class<TBody>? = null,
    ): APIClientResponse<TBody?> {
        return webClient
            .get()
            .uri { uriBuilder ->
                var builder = uriBuilder.path(url)
                queryParams?.forEach { (key, value) ->
                    builder = builder.queryParam(key, value)
                }
                builder.build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .apply {
                withAuth(user)
            }
            .exchange()
            .toAPIClientResponse(responseBodyClass)
    }


    fun <TBody> post(
        url: String,
        body: Any?,
        user: MockedAuthenticatedUserUpdated?,
        responseBodyClass: Class<TBody>? = null,
    ): APIClientResponse<TBody?> {
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
            .toAPIClientResponse(responseBodyClass)
    }


    fun <TBody> put(
        url: String,
        body: TBody?,
        user: MockedAuthenticatedUserUpdated?,
        responseBodyClass: Class<TBody>? = null,
    ): APIClientResponse<TBody?> {
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
            .toAPIClientResponse(responseBodyClass)
    }


    fun <TBody> delete(
        url: String,
        user: MockedAuthenticatedUserUpdated?,
        responseBodyClass: Class<TBody>? = null,
    ): APIClientResponse<TBody?> {
        return webClient
            .delete()
            .uri(url)
            .apply {
                withAuth(user)
            }
            .exchange()
            .toAPIClientResponse(responseBodyClass)
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


    private fun <TBody> WebTestClient.ResponseSpec.toAPIClientResponse(
        responseBodyClass: Class<TBody>? = null
    ): APIClientResponse<TBody?> {
        val result = this.returnResult<Unit>()

        val responseBody: TBody? = try {
            if (responseBodyClass == null) {
                null
            } else {
                this.expectBody(responseBodyClass)
                    .returnResult()
                    .responseBody
            }
        } catch (ex: Exception) {
            null
        }

        return APIClientResponse(
            body = responseBody,
            status = result.status,
            headers = result.responseHeaders,
            cookies = result.responseCookies
        )
    }
}