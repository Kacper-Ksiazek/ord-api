package com.ord.core.gpt_tokens_usage_analytics.repositories

import com.ord.core.gpt_tokens_usage_analytics.responses.dto.DailyTokensUsageDTO
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageRecordDTO
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.TokensUsageByOperationTypeDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.UUID

interface GptTokensUsageAnalyticsRepository {
    fun findPaginated(userId: UUID, page: Int, perPage: Int): Mono<PaginatedDataResponse<GptTokensUsageRecordDTO>>
    fun getDailyUsage(userId: UUID, since: Instant?): Flux<DailyTokensUsageDTO>
    fun getUsageByOperationType(userId: UUID, since: Instant?): Flux<TokensUsageByOperationTypeDTO>
}
