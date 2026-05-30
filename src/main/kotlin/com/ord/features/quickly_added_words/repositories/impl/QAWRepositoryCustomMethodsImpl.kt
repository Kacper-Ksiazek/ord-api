package com.ord.features.quickly_added_words.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.features.quickly_added_words.repositories.QAWApprovalCounts
import com.ord.features.quickly_added_words.repositories.QAWRepositoryCustomMethods
import com.ord.features.quickly_added_words.repositories.QAWPaginatedResult
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

    override fun countByApprovalStatus(userId: UUID): Mono<QAWApprovalCounts> {
        // language=SQL
        val query = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN is_approved THEN 1 ELSE 0 END) AS approved_count,
                SUM(CASE WHEN NOT is_approved THEN 1 ELSE 0 END) AS unapproved_count
            FROM quickly_added_words
            WHERE user_id = :userId
        """

        return databaseClient
            .sql(query)
            .bind("userId", userId)
            .map { row ->
                QAWApprovalCounts(
                    total = row.get("total", Long::class.java)!!,
                    approvedCount = row.get("approved_count", Long::class.java)!!,
                    unapprovedCount = row.get("unapproved_count", Long::class.java)!!,
                )
            }
            .one()
    }

    override fun findManyQAWs(
        userId: UUID,
        page: Int?,
        perPage: Int?,
        isApproved: Boolean?,
    ): Mono<QAWPaginatedResult> {
        val actualPage = (page ?: 0).coerceAtLeast(0)
        val actualPerPage = (perPage ?: 50).coerceIn(1, 100)
        val offset = actualPage * actualPerPage

        val approvalFilterClause = when (isApproved) {
            true -> " AND is_approved = TRUE"
            false -> " AND is_approved = FALSE"
            null -> ""
        }

        // language=SQL
        val countQuery = """
            SELECT COUNT(*)
            FROM quickly_added_words
            WHERE user_id = :userId$approvalFilterClause
        """

        // language=SQL
        val selectQuery = """
            SELECT id, word, language, translation, definition, extra_mark, type, is_approved, created_at, user_id
            FROM quickly_added_words
            WHERE user_id = :userId$approvalFilterClause
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """

        // language=SQL
        val unapprovedCountQuery = """
            SELECT COUNT(*)
            FROM quickly_added_words
            WHERE user_id = :userId AND is_approved = FALSE
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
                    translation = row.get("translation", String::class.java),
                    definition = row.get("definition", String::class.java),
                    extraMark = row.get("extra_mark", String::class.java)?.let(WordExtraMark::valueOf),
                    type = row.get("type", String::class.java)?.let(WordType::valueOf),
                    isApproved = row.get("is_approved", Boolean::class.java)!!,
                    createdAt = row.get("created_at", Instant::class.java)!!,
                    userId = row.get("user_id", UUID::class.java)!!
                )
            }
            .all()
            .collectList()

        fun toPaginatedResult(
            words: List<QuicklyAddedWordEntity>,
            totalItems: Long,
            unapprovedCount: Long?,
        ) = QAWPaginatedResult(
            paginated = PaginatedDataResponse(
                data = words,
                pagination = PaginationData(
                    page = actualPage,
                    perPage = actualPerPage,
                    totalResults = totalItems,
                    resultsOnCurrentPage = words.size
                )
            ),
            unapprovedCount = unapprovedCount,
        )

        return if (isApproved == null) {
            val unapprovedCountResult: Mono<Long> = databaseClient
                .sql(unapprovedCountQuery)
                .bind("userId", userId)
                .map { row -> row.get(0, Long::class.java)!! }
                .one()

            Mono.zip(selectQueryResult, countQueryResult, unapprovedCountResult)
                .map { t -> toPaginatedResult(t.t1, t.t2, t.t3) }
        } else {
            Mono.zip(selectQueryResult, countQueryResult)
                .map { t -> toPaginatedResult(t.t1, t.t2, unapprovedCount = null) }
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

    override fun findAllWordsByUserIdAndLanguage(
        userId: UUID,
        language: LanguageName
    ): Flux<String> {
        // language=SQL
        val query = """
            SELECT word
            FROM quickly_added_words
            WHERE user_id = :userId AND language = :language
        """

        return databaseClient
            .sql(query)
            .bind("userId", userId)
            .bind("language", language.name)
            .map { row -> row.get("word", String::class.java)!! }
            .all()
    }
}