package com.ord.testing_utils.api.clients

import com.ord.core.gpt_tokens_usage_analytics.requests.dto.AnalyticsPeriod
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageAnalyticsResponse
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageRecordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class GptTokensUsageAnalyticsAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/gpt-tokens-usage"

    fun getRecords(
        page: Int? = null,
        perPage: Int? = null,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<PaginatedDataResponse<GptTokensUsageRecordDTO>?> {
        val queryParams = mutableMapOf<String, String>()
        page?.let { queryParams["page"] = it.toString() }
        perPage?.let { queryParams["perPage"] = it.toString() }

        return get(
            url = "$baseUrl/",
            user = user,
            queryParams = queryParams,
            responseBodyType = object : ParameterizedTypeReference<PaginatedDataResponse<GptTokensUsageRecordDTO>>() {}
        )
    }

    fun getAnalytics(
        period: AnalyticsPeriod,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<GptTokensUsageAnalyticsResponse?> {
        val queryParams = mutableMapOf<String, String>()
        queryParams["period"] = period.name

        return get(
            url = "$baseUrl/analytics",
            user = user,
            queryParams = queryParams,
            responseBodyType = object : ParameterizedTypeReference<GptTokensUsageAnalyticsResponse>() {}
        )
    }
}
