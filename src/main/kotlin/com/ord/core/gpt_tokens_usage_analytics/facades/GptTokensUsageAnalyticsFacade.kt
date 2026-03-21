package com.ord.core.gpt_tokens_usage_analytics.facades

import com.ord.core.gpt_tokens_usage_analytics.requests.dto.AnalyticsPeriod
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageAnalyticsResponse
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageRecordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface GptTokensUsageAnalyticsFacade {
    fun getRecords(
        userId: UUID,
        page: Int?,
        perPage: Int?
    ): Mono<ResponseEntity<PaginatedDataResponse<GptTokensUsageRecordDTO>>>

    fun getAnalytics(
        userId: UUID,
        period: AnalyticsPeriod
    ): Mono<ResponseEntity<GptTokensUsageAnalyticsResponse>>
}
