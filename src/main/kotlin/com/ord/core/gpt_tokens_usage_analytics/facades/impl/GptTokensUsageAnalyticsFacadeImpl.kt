package com.ord.core.gpt_tokens_usage_analytics.facades.impl

import com.ord.core.gpt_tokens_usage_analytics.facades.GptTokensUsageAnalyticsFacade
import com.ord.core.gpt_tokens_usage_analytics.repositories.GptTokensUsageAnalyticsRepository
import com.ord.core.gpt_tokens_usage_analytics.requests.dto.AnalyticsPeriod
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageAnalyticsResponse
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageRecordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class GptTokensUsageAnalyticsFacadeImpl(
    private val repository: GptTokensUsageAnalyticsRepository
) : GptTokensUsageAnalyticsFacade {

    override fun getRecords(
        userId: UUID,
        page: Int?,
        perPage: Int?
    ): Mono<ResponseEntity<PaginatedDataResponse<GptTokensUsageRecordDTO>>> {
        return repository
            .findPaginated(
                userId = userId,
                page = page ?: 1,
                perPage = perPage ?: 50
            )
            .map { ResponseEntity.ok(it) }
    }

    override fun getAnalytics(
        userId: UUID,
        period: AnalyticsPeriod
    ): Mono<ResponseEntity<GptTokensUsageAnalyticsResponse>> {
        val since = period.toInstant()

        val dailyUsage = repository.getDailyUsage(userId, since).collectList()
        val usageByOperationType = repository.getUsageByOperationType(userId, since).collectList()

        return Mono
            .zip(dailyUsage, usageByOperationType)
            .map { t ->
                GptTokensUsageAnalyticsResponse(
                    dailyUsage = t.t1,
                    usageByOperationType = t.t2
                )
            }
            .map { ResponseEntity.ok(it) }
    }
}
