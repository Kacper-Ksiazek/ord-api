package com.ord.core.gpt_tokens_usage_analytics.repositories.impl

import com.ord.core.gpt_tokens_usage_analytics.repositories.GptTokensUsageAnalyticsRepository
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.DailyTokensUsageDTO
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageRecordDTO
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.TokensUsageByOperationTypeDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.api.dto.responses.PaginationData
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Repository
class GptTokensUsageAnalyticsRepositoryImpl(
    template: R2dbcEntityTemplate
) : GptTokensUsageAnalyticsRepository {
    private val databaseClient: DatabaseClient = template.databaseClient

    override fun findPaginated(
        userId: UUID,
        page: Int,
        perPage: Int
    ): Mono<PaginatedDataResponse<GptTokensUsageRecordDTO>> {
        val actualPage = page.coerceAtLeast(1)
        val actualPerPage = perPage.coerceIn(1, 100)
        val offset = maxOf(0, (actualPage - 1) * actualPerPage)

        // language=SQL
        val countQuery = """
            SELECT COUNT(*)
            FROM gpt_tokens_usage
            WHERE user_id = :userId
        """

        // language=SQL
        val selectQuery = """
            SELECT id, operation_type, model, input_tokens, output_tokens, created_at
            FROM gpt_tokens_usage
            WHERE user_id = :userId
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """

        val countQueryResult: Mono<Long> = databaseClient
            .sql(countQuery)
            .bind("userId", userId)
            .map { row -> row.get(0, Long::class.java)!! }
            .one()

        val selectQueryResult: Mono<List<GptTokensUsageRecordDTO>> = databaseClient
            .sql(selectQuery)
            .bind("userId", userId)
            .bind("limit", actualPerPage)
            .bind("offset", offset)
            .map { row ->
                GptTokensUsageRecordDTO(
                    id = row.get("id", UUID::class.java)!!,
                    operationType = row.get("operation_type", String::class.java)!!,
                    model = row.get("model", String::class.java)!!,
                    inputTokens = row.get("input_tokens", Int::class.java)!!,
                    outputTokens = row.get("output_tokens", Int::class.java)!!,
                    createdAt = row.get("created_at", Instant::class.java)!!
                )
            }
            .all()
            .collectList()

        return Mono
            .zip(selectQueryResult, countQueryResult)
            .map { t ->
                val records = t.t1
                val totalItems = t.t2

                PaginatedDataResponse(
                    data = records,
                    pagination = PaginationData(
                        page = actualPage,
                        perPage = actualPerPage,
                        totalResults = totalItems,
                        resultsOnCurrentPage = records.size
                    )
                )
            }
    }

    override fun getDailyUsage(userId: UUID, since: Instant?): Flux<DailyTokensUsageDTO> {
        // language=SQL
        val query = if (since != null) """
            SELECT DATE(created_at) as date, SUM(input_tokens) as input_tokens, SUM(output_tokens) as output_tokens
            FROM gpt_tokens_usage
            WHERE user_id = :userId AND created_at >= :since
            GROUP BY DATE(created_at)
            ORDER BY date
        """ else """
            SELECT DATE(created_at) as date, SUM(input_tokens) as input_tokens, SUM(output_tokens) as output_tokens
            FROM gpt_tokens_usage
            WHERE user_id = :userId
            GROUP BY DATE(created_at)
            ORDER BY date
        """

        val spec = databaseClient
            .sql(query)
            .bind("userId", userId)

        val boundSpec = if (since != null) spec.bind("since", since) else spec

        return boundSpec
            .map { row ->
                DailyTokensUsageDTO(
                    date = row.get("date", LocalDate::class.java)!!,
                    inputTokens = row.get("input_tokens", Long::class.java)!!,
                    outputTokens = row.get("output_tokens", Long::class.java)!!
                )
            }
            .all()
    }

    override fun getUsageByOperationType(userId: UUID, since: Instant?): Flux<TokensUsageByOperationTypeDTO> {
        // language=SQL
        val query = if (since != null) """
            SELECT operation_type, SUM(input_tokens) as input_tokens, SUM(output_tokens) as output_tokens, COUNT(*) as count
            FROM gpt_tokens_usage
            WHERE user_id = :userId AND created_at >= :since
            GROUP BY operation_type
            ORDER BY SUM(input_tokens + output_tokens) DESC
        """ else """
            SELECT operation_type, SUM(input_tokens) as input_tokens, SUM(output_tokens) as output_tokens, COUNT(*) as count
            FROM gpt_tokens_usage
            WHERE user_id = :userId
            GROUP BY operation_type
            ORDER BY SUM(input_tokens + output_tokens) DESC
        """

        val spec = databaseClient
            .sql(query)
            .bind("userId", userId)

        val boundSpec = if (since != null) spec.bind("since", since) else spec

        return boundSpec
            .map { row ->
                TokensUsageByOperationTypeDTO(
                    operationType = row.get("operation_type", String::class.java)!!,
                    inputTokens = row.get("input_tokens", Long::class.java)!!,
                    outputTokens = row.get("output_tokens", Long::class.java)!!,
                    count = row.get("count", Long::class.java)!!
                )
            }
            .all()
    }
}
