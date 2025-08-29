package com.ord.testing_utils.api.clients

import com.ord.core.word.api.requests.dto.*
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.util.*

class WordsAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/words"

    fun getAllWords(
        body: GetManyWordsRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<PaginatedDataResponse<WordListItem>> {
        val response = post("$baseUrl/get-many-words", body, user)
            .expectBody(object : ParameterizedTypeReference<PaginatedDataResponse<WordListItem>>() {})
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun getWord(
        id: UUID,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<SingleWordResponse> {
        val response = get("$baseUrl/$id", user)
            .expectBody(SingleWordResponse::class.java)
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,

            )
    }

    fun createWord(
        body: CreateWordRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<WordDTO> {
        val response = post("$baseUrl/", body, user)
            .expectBody(WordDTO::class.java)
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun updateWord(
        id: UUID,
        body: UpdateWordRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<WordDTO> {
        val response = webClient
            .patch()
            .uri("$baseUrl/$id")
            .contentType(MediaType.APPLICATION_JSON)
            .apply {
                if (user != null) {
                    this.cookie(user.authCookie.name, user.authCookie.value)
                }
                this.bodyValue(body)
            }
            .exchange()
            .expectBody(WordDTO::class.java)
            .returnResult()

        return APIClientResponse(
            body = response.responseBody,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun deleteWord(
        id: UUID,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit> {
        val response = delete("$baseUrl/$id", user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun changeWordBank(
        id: UUID,
        body: ChangeBankForSingleWordRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit> {
        val response = post("$baseUrl/$id/change-bank", body, user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun changeBankForMultipleWords(
        body: ChangeBankForMultipleWordsRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit> {
        val response = post("$baseUrl/change-bank-for-multiple-words", body, user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun togglePropertyForOneWord(
        id: UUID,
        property: WordToggleableProperty? = null,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit> {
        val url = if (property != null) {
            "$baseUrl/$id/toggle-property?property=$property"
        } else {
            "$baseUrl/$id/toggle-property"
        }

        val response = post(url, null, user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }

    fun togglePropertyForManyWords(
        property: WordToggleableProperty? = null,
        body: WordBulkActionRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit> {
        val url = if (property != null) {
            "$baseUrl/toggle-property-for-multiple-words?property=$property"
        } else {
            "$baseUrl/toggle-property-for-multiple-words"
        }

        val response = post(url, body, user)
            .returnResult<Unit>()

        return APIClientResponse(
            body = null,
            status = response.status,
            headers = response.responseHeaders,
            cookies = response.responseCookies,
        )
    }
}