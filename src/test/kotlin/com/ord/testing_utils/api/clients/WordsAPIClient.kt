package com.ord.testing_utils.api.clients

import com.ord.core.word.api.crud.requests.dto.ChangeBankForMultipleWordsRequest
import com.ord.core.word.api.crud.requests.dto.ChangeBankForSingleWordRequest
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.dto.UnsafeGetManyWordsRequest
import com.ord.core.word.api.crud.requests.dto.UpdateWordRequest
import com.ord.core.word.api.crud.requests.dto.WordBulkActionRequest
import com.ord.core.word.api.crud.requests.enums.WordToggleableProperty
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.WordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class WordsAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/words"

    fun getManyWords(
        body: UnsafeGetManyWordsRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<PaginatedDataResponse<WordListItem>?> {
        return post(
            url = "$baseUrl/get-many-words",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<PaginatedDataResponse<WordListItem>>() {}
        )
    }


    fun getWord(
        id: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<SingleWordResponse?> {
        return get(
            url = "$baseUrl/$id",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<SingleWordResponse>() {}
        )
    }


    fun createWord(
        body: CreateWordRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<WordDTO?> {
        return post(
            url = "$baseUrl/",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDTO>() {}
        )
    }


    fun updateWord(
        id: UUID,
        body: UpdateWordRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<WordDTO?> {
        return patch(
            url = "$baseUrl/$id",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDTO>() {}
        )
    }


    fun deleteWord(
        id: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return delete<Unit>(
            url = "$baseUrl/$id",
            user = user
        )
    }


    fun changeWordBank(
        id: UUID,
        body: ChangeBankForSingleWordRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return post(
            url = "$baseUrl/$id/change-bank",
            body = body,
            user = user
        )
    }


    fun changeBankForMultipleWords(
        body: ChangeBankForMultipleWordsRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return post(
            url = "$baseUrl/change-bank-for-multiple-words",
            body = body,
            user = user
        )
    }


    fun togglePropertyForOneWord(
        id: UUID,
        property: WordToggleableProperty? = null,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        val queryParam = if (property != null) "?property=$property" else ""
        return post(
            url = "$baseUrl/$id/toggle-property$queryParam",
            body = null,
            user = user
        )
    }


    fun togglePropertyForManyWords(
        property: WordToggleableProperty? = null,
        body: WordBulkActionRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        val queryParam = if (property != null) "?property=$property" else ""
        return post(
            url = "$baseUrl/toggle-property-for-multiple-words$queryParam",
            body = body,
            user = user
        )
    }
}