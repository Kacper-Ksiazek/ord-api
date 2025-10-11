package com.ord.testing_utils.api.clients

import com.ord.features.quickly_added_words.api.requests.ApproveManyQAWRequest
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.UpdateQAWRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class QAWAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/quickly-added-words"

    fun createOne(
        body: CreateQAWRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<QuicklyAddedWordDTO?> {
        return post(
            url = "$baseUrl/",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<QuicklyAddedWordDTO>() {}
        )
    }

    fun bulkCreate(
        body: List<CreateQAWRequest>,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<List<QuicklyAddedWordDTO>?> {
        return post(
            url = "$baseUrl/bulk-create",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<QuicklyAddedWordDTO>>() {}
        )
    }

    fun getManyQAWs(
        page: Int? = null,
        perPage: Int? = null,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<PaginatedDataResponse<QuicklyAddedWordDTO>?> {
        val queryParams = mutableMapOf<String, String>()
        page?.let { queryParams["page"] = it.toString() }
        perPage?.let { queryParams["perPage"] = it.toString() }

        return get(
            url = "$baseUrl/",
            user = user,
            queryParams = queryParams,
            responseBodyType = object : ParameterizedTypeReference<PaginatedDataResponse<QuicklyAddedWordDTO>>() {}
        )
    }

    fun updateOne(
        id: UUID,
        body: UpdateQAWRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<QuicklyAddedWordDTO?> {
        return patch(
            url = "$baseUrl/$id",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<QuicklyAddedWordDTO>() {}
        )
    }

    fun bulkUpdate(
        body: Map<UUID, String>,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<List<QuicklyAddedWordDTO>?> {
        return patch(
            url = "$baseUrl/bulk-update",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<QuicklyAddedWordDTO>>() {}
        )
    }

    fun approveMany(
        body: ApproveManyQAWRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return patch(
            url = "$baseUrl/approve-many",
            body = body,
            user = user
        )
    }

    fun deleteOne(
        id: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return delete<Unit>(
            url = "$baseUrl/$id",
            user = user
        )
    }

    fun bulkDelete(
        body: List<UUID>,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return post(
            url = "$baseUrl/bulk-delete",
            body = body,
            user = user
        )
    }
}
