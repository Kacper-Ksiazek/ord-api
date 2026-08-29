package com.ord.testing_utils.api.clients

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class WordCaptureAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/words"

    fun captureOne(
        body: CaptureWordRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordDTO?> {
        return post(
            url = "$baseUrl/capture",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDTO>() {},
        )
    }

    fun bulkCapture(
        body: List<CaptureWordRequest>,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<List<WordDTO>?> {
        return post(
            url = "$baseUrl/bulk-capture",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<WordDTO>>() {},
        )
    }

    fun getCapturedWords(
        page: Int? = null,
        perPage: Int? = null,
        status: WordStatus? = null,
        language: LanguageName? = null,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordsPaginatedDataResponse?> {
        val queryParams = mutableMapOf<String, String>()
        page?.let { queryParams["page"] = it.toString() }
        perPage?.let { queryParams["perPage"] = it.toString() }
        status?.let { queryParams["status"] = it.name }
        language?.let { queryParams["language"] = it.name }

        return get(
            url = "$baseUrl/captured",
            user = user,
            queryParams = queryParams,
            responseBodyType = object : ParameterizedTypeReference<WordsPaginatedDataResponse>() {},
        )
    }

    fun getOverview(
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordOverviewResponse?> {
        return get(
            url = "$baseUrl/overview",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordOverviewResponse>() {},
        )
    }

    fun updateCaptured(
        id: UUID,
        body: UpdateCapturedWordRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordDTO?> {
        return patch(
            url = "$baseUrl/$id/capture",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDTO>() {},
        )
    }

    fun bulkUpdateSource(
        body: Map<UUID, String>,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<List<WordDTO>?> {
        return patch(
            url = "$baseUrl/bulk-update-source",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<WordDTO>>() {},
        )
    }

    fun activateOne(
        id: UUID,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordDTO?> {
        return patch(
            url = "$baseUrl/$id/activate",
            body = null,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDTO>() {},
        )
    }

    fun activateMany(
        body: ActivateManyWordsRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<Unit?> {
        return patch(
            url = "$baseUrl/activate-many",
            body = body,
            user = user,
        )
    }
}
