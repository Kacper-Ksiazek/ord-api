package com.ord.testing_utils.api

import com.ord.shared.utils.Console
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

abstract class APITestClient(
    val webClient: WebTestClient
) {
    fun <TResponseBody> get(
        url: String,
        user: MockedAuthenticatedUser?,
        queryParams: Map<String, String>? = null,
        responseBodyType: ParameterizedTypeReference<TResponseBody>? = null,
    ): APIClientResponse<TResponseBody?> {
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
            .toAPIClientResponse(responseBodyType)
    }


    fun <TResponseBody> post(
        url: String,
        body: Any?,
        user: MockedAuthenticatedUser?,
        responseBodyType: ParameterizedTypeReference<TResponseBody>? = null,
    ): APIClientResponse<TResponseBody?> {
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
            .toAPIClientResponse(responseBodyType)
    }


    fun <TResponseBody> put(
        url: String,
        body: Any?,
        user: MockedAuthenticatedUser?,
        responseBodyType: ParameterizedTypeReference<TResponseBody>? = null,
    ): APIClientResponse<TResponseBody?> {
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
            .toAPIClientResponse(responseBodyType)
    }


    fun <TResponseBody> delete(
        url: String,
        user: MockedAuthenticatedUser?,
        responseBodyType: ParameterizedTypeReference<TResponseBody>? = null,
    ): APIClientResponse<TResponseBody?> {
        return webClient
            .delete()
            .uri(url)
            .apply {
                withAuth(user)
            }
            .exchange()
            .toAPIClientResponse(responseBodyType)
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


    private fun <TResponseBody> WebTestClient.ResponseSpec.toAPIClientResponse(
        responseBodyType: ParameterizedTypeReference<TResponseBody>? = null
    ): APIClientResponse<TResponseBody?> {
        val result = this.returnResult<Unit>()

        val responseBody: TResponseBody? = try {
            if (responseBodyType == null) {
                null
            } else {
                this.expectBody(responseBodyType)
                    .returnResult()
                    .responseBody
            }
        } catch (ex: Exception) {
            Console.printRed("\uD83D\uDEA8 [e2e] Failed to convert response body")
            Console.printRed("Exception type: ${ex::class.simpleName}")
            Console.printRed("Exception message: ${ex.message}")
            Console.printRed("HTTP Status: ${result.status}")
            Console.printRed("Expected response type: ${responseBodyType?.type}")
            Console.printRed("Response headers: ${result.responseHeaders}")
            Console.printRed("Raw response body:")

            val rawBody = try {
                // Try to get the raw response as string for debugging
                this.expectBody(String::class.java)
                    .returnResult()
                    .responseBody ?: "Empty response body"
            } catch (bodyEx: Exception) {
                "Failed to read raw body: ${bodyEx.message}"
            }

            Console.printRed("  $rawBody")
            Console.printRed("Stack trace:")
            ex.printStackTrace()
            Console.printRed("--- End of error details ---")

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