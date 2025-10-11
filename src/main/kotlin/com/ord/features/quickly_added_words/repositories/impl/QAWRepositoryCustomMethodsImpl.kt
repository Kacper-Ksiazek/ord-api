package com.ord.features.quickly_added_words.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.features.quickly_added_words.repositories.QAWRepositoryCustomMethods
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.api.dto.responses.PaginationData
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.UUID

@Repository
class QAWRepositoryCustomMethodsImpl(
    template: R2dbcEntityTemplate
) : QAWRepositoryCustomMethods {
    private val databaseClient: DatabaseClient = template.databaseClient

    override fun findManyQAWs(
        userId: UUID,
        page: Int?,
        perPage: Int?
    ): Mono<PaginatedDataResponse<QuicklyAddedWordEntity>> {
        val actualPage = (page ?: 1).coerceAtLeast(1)
        val actualPerPage = (perPage ?: 50).coerceIn(1, 100)
        val offset = maxOf(0, (actualPage - 1) * actualPerPage)

        // language=SQL
        val countQuery = """
            SELECT COUNT(*)
            FROM quickly_added_words
            WHERE user_id = :userId
        """

        // language=SQL
        val selectQuery = """
            SELECT id, word, language, created_at, user_id
            FROM quickly_added_words
            WHERE user_id = :userId
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """

        val countQueryResult: Mono<Long> = databaseClient
            .sql(countQuery)
            .bind("userId", userId)
            .map { row -> row.get(0, Long::class.java)!! }
            .one()

        val selectQueryResult: Mono<List<QuicklyAddedWordEntity>> = databaseClient
            .sql(selectQuery)
            .bind("userId", userId)
            .bind("limit", actualPerPage)
            .bind("offset", offset)
            .map { row ->
                QuicklyAddedWordEntity(
                    id = row.get("id", UUID::class.java)!!,
                    word = row.get("word", String::class.java)!!,
                    language = LanguageName.valueOf(row.get("language", String::class.java)!!),
                    createdAt = row.get("created_at", Instant::class.java)!!,
                    userId = row.get("user_id", UUID::class.java)!!
                )
            }
            .all()
            .collectList()

        return Mono
            .zip(selectQueryResult, countQueryResult)
            .map { t ->
                val words = t.t1
                val totalItems = t.t2

                PaginatedDataResponse(
                    data = words,
                    pagination = PaginationData(
                        page = actualPage,
                        perPage = actualPerPage,
                        totalResults = totalItems,
                        resultsOnCurrentPage = words.size
                    )
                )
            }
    }

    override fun approveManyByIdsAndUserId(
        ids: Set<UUID>,
        userId: UUID
    ): Mono<Unit> {
        if (ids.isEmpty()) {
            return Mono.error(com.ord.exceptions.REST.BadRequestException("No IDs provided for approval"))
        }

        // language=SQL
        val updateQuery = """
            UPDATE quickly_added_words
            SET is_approved = true
            WHERE id = ANY(:ids) AND user_id = :userId
        """

        return databaseClient
            .sql(updateQuery)
            .bind("ids", ids.toTypedArray())
            .bind("userId", userId)
            .fetch()
            .rowsUpdated()
            .then(Mono.just(Unit))
    }
}