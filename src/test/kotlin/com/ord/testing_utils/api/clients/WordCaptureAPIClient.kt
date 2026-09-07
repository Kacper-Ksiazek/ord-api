package com.ord.testing_utils.api.clients

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.capture.requests.dto.ActivateManyWordsRequest
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
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

    fun capture(
        body: List<CaptureWordRequest>,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<List<WordDTO>?> {
        return post(
            url = "$baseUrl/capture",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<WordDTO>>() {},
        )
    }

    fun captureOne(
        body: CaptureWordRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordDTO?> {
        val response = capture(listOf(body), user)
        return APIClientResponse(
            body = response.body?.firstOrNull(),
            status = response.status,
            headers = response.headers,
            cookies = response.cookies,
        )
    }

    fun listWords(
        language: LanguageName? = null,
        page: Int? = null,
        perPage: Int? = null,
        isFromUnverifiedSource: Boolean? = null,
        hasProgress: Boolean? = null,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordsPaginatedDataResponse?> {
        val queryParams = mutableMapOf<String, String>()
        language?.let { queryParams["language"] = it.name }
        page?.let { queryParams["page"] = it.toString() }
        perPage?.let { queryParams["perPage"] = it.toString() }
        isFromUnverifiedSource?.let { queryParams["isFromUnverifiedSource"] = it.toString() }
        hasProgress?.let { queryParams["hasProgress"] = it.toString() }

        return get(
            url = baseUrl,
            user = user,
            queryParams = queryParams,
            responseBodyType = object : ParameterizedTypeReference<WordsPaginatedDataResponse>() {},
        )
    }

    fun getOverview(
        user: MockedAuthenticatedUser? = null,
        language: LanguageName? = null,
    ): APIClientResponse<WordOverviewResponse?> {
        val queryParams = mutableMapOf<String, String>()
        language?.let { queryParams["language"] = it.name }

        return get(
            url = "$baseUrl/overview",
            user = user,
            queryParams = queryParams,
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
