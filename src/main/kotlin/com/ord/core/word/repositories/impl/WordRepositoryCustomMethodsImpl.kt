package com.ord.core.word.repositories.impl

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.crud.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.core.word.repositories.WordOverviewCounts
import com.ord.core.word.repositories.WordRepositoryCustomMethods
import com.ord.core.word.repositories.WordsPaginatedResult
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.bank.dto.BankCompact
import com.ord.features.bank_group.dto.BankGroupCompact
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.api.dto.responses.PaginationData
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import io.r2dbc.spi.Readable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Repository
class WordRepositoryCustomMethodsImpl(
    template: R2dbcEntityTemplate,
) : WordRepositoryCustomMethods {
    private val databaseClient: DatabaseClient = template.databaseClient

    override fun findOneWord(wordId: UUID, userId: UUID): Mono<SingleWordResponse> {
        val selectQuery = """
            SELECT
                ${SingleWordResponse.fields.joinToString(", ") { "words.$it" }},
                wp.points AS wp_points,
                wp.completed_at AS wp_completed_at,
                wp.first_completed_at AS wp_first_completed_at,
                ${BankCompact.fields.joinToString(", ") { "banks.$it AS bank_$it" }},
                ${BankGroupCompact.fields.joinToString(", ") { "bank_groups.$it AS bank_group_$it" }}
            FROM words
                LEFT JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id
                LEFT JOIN banks ON words.bank_id = banks.id
                LEFT JOIN bank_groups ON banks.group_id = bank_groups.id
            WHERE words.id = :wordId AND words.user_id = :userId
        """

        return databaseClient.sql(selectQuery)
            .bind("wordId", wordId)
            .bind("userId", userId)
            .map { row -> mapSingleWordResponse(row) }
            .one()
            .switchIfEmpty(Mono.error(NotFoundException("Word with id $wordId not found for user with id $userId")))
    }

    override fun findManyWords(
        userId: UUID,
        language: LanguageName,
        isFromUnverifiedSource: Boolean?,
        hasProgress: Boolean?,
        completed: Boolean?,
        bookmarked: Boolean?,
        searchingPhrase: String?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordTypes: Set<WordType>?,
        wordExtraMarks: Set<WordExtraMark>?,
        sortDirection: SortDirection,
        sortBy: GetAllWordsSortOptions,
        page: Int,
        perPage: Int,
        includeUnverifiedSourceCount: Boolean,
    ): Mono<WordsPaginatedResult> {
        val whereClause = createQueryConditions(
            isFromUnverifiedSource,
            hasProgress,
            completed,
            searchingPhrase,
            bookmarked,
            banksIds,
            bankGroupsIds,
            wordTypes,
            wordExtraMarks,
        )
        val valuesBindings = createValuesBindings(
            userId,
            language,
            isFromUnverifiedSource,
            searchingPhrase,
            bookmarked,
            banksIds,
            bankGroupsIds,
            wordTypes,
            wordExtraMarks,
        )
        val orderByClause = resolveWordsOrderByClause(sortBy, sortDirection)

        val countQuery = buildString {
            appendLine("SELECT COUNT(*)")
            appendLine("FROM words")
            appendLine("    LEFT JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id")
            append("WHERE ")
            append(whereClause)
        }

        val selectQuery = buildString {
            appendLine(
                """
                SELECT
                    ${WordListItem.fields.joinToString(", ") { "words.$it" }},
                    wp.points AS wp_points,
                    wp.completed_at AS wp_completed_at,
                    wp.first_completed_at AS wp_first_completed_at,
                    ${BankCompact.fields.joinToString(", ") { "banks.$it AS bank_$it" }},
                    ${BankGroupCompact.fields.joinToString(", ") { "bank_groups.$it AS bank_group_$it" }}
                FROM words
                    LEFT JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id
                    LEFT JOIN banks ON words.bank_id = banks.id
                    LEFT JOIN bank_groups ON banks.group_id = bank_groups.id
                """.trimIndent(),
            )
            append("WHERE ")
            append(whereClause)
            appendLine()
            appendLine(orderByClause)
            appendLine("LIMIT :limit OFFSET :offset")
        }

        val unverifiedSourceCountQuery = """
            SELECT COUNT(*)
            FROM words
            WHERE words.user_id = :userId
              AND words.is_from_unverified_source = TRUE
              AND words.language = :language
        """

        val countQueryResult = databaseClient.sql(countQuery)
            .bindValues(valuesBindings)
            .map { row -> row.get(0, Long::class.java)!! }
            .one()

        val selectQueryResult = databaseClient.sql(selectQuery)
            .bindValues(valuesBindings)
            .bind("limit", perPage)
            .bind("offset", maxOf(0, page * perPage))
            .map { row -> mapWordListItem(row) }
            .all()
            .collectList()

        fun toResult(words: List<WordListItem>, totalItems: Long, unverifiedSourceCount: Long?) = WordsPaginatedResult(
            paginated = PaginatedDataResponse(
                data = words,
                pagination = PaginationData(
                    page = page,
                    perPage = perPage,
                    totalResults = totalItems,
                    resultsOnCurrentPage = words.size,
                ),
            ),
            unverifiedSourceCount = unverifiedSourceCount,
        )

        return if (includeUnverifiedSourceCount) {
            val unverifiedSourceCountResult = databaseClient.sql(unverifiedSourceCountQuery)
                .bind("userId", userId)
                .bind("language", language.name)
                .map { row -> row.get(0, Long::class.java)!! }
                .one()

            Mono.zip(selectQueryResult, countQueryResult, unverifiedSourceCountResult)
                .map { t -> toResult(t.t1, t.t2, t.t3) }
        } else {
            Mono.zip(selectQueryResult, countQueryResult)
                .map { t -> toResult(t.t1, t.t2, unverifiedSourceCount = null) }
        }
    }

    override fun countOverview(userId: UUID): Mono<WordOverviewCounts> {
        val query = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN wp.id IS NOT NULL THEN 1 ELSE 0 END), 0) AS active_count,
                COALESCE(SUM(CASE WHEN words.is_from_unverified_source = TRUE THEN 1 ELSE 0 END), 0) AS unverified_source_count
            FROM words
                LEFT JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id
            WHERE words.user_id = :userId
        """

        return databaseClient.sql(query)
            .bind("userId", userId)
            .map { row ->
                WordOverviewCounts(
                    total = row.get("total", Long::class.java)!!,
                    activeCount = row.get("active_count", Long::class.java)!!,
                    unverifiedSourceCount = row.get("unverified_source_count", Long::class.java)!!,
                )
            }
            .one()
    }

    override fun findNOfLatestWords(userId: UUID, language: LanguageName, limit: Int): Flux<String> {
        val selectQuery = """
            SELECT source_word
            FROM words
            WHERE language = :language AND user_id = :userId
            ORDER BY created_at DESC
            LIMIT :limit
        """

        return databaseClient.sql(selectQuery)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row -> row.get("source_word", String::class.java)!! }
            .all()
    }

    override fun findNOfMostDifficultWords(userId: UUID, language: LanguageName, limit: Int): Flux<String> {
        val selectQuery = """
            SELECT w.source_word
            FROM words w
                INNER JOIN word_progress wp ON wp.word_id = w.id AND wp.user_id = w.user_id
            WHERE w.language = :language
              AND w.user_id = :userId
            ORDER BY wp.points ASC
            LIMIT :limit
        """

        return databaseClient.sql(selectQuery)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row -> row.get("source_word", String::class.java)!! }
            .all()
    }

    override fun findAllWordsFromBanks(userId: UUID, language: LanguageName, banksIds: List<UUID>): Flux<String> {
        val selectQuery = """
            SELECT source_word
            FROM words
            WHERE language = :language
              AND user_id = :userId
              AND bank_id = ANY(:banksIds)
        """

        return databaseClient.sql(selectQuery)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("banksIds", banksIds.toTypedArray())
            .map { row -> row.get("source_word", String::class.java)!! }
            .all()
    }

    override fun findAllSourceWordsByUserIdAndLanguage(userId: UUID, language: LanguageName): Flux<String> {
        val query = """
            SELECT source_word
            FROM words
            WHERE user_id = :userId AND language = :language
        """

        return databaseClient.sql(query)
            .bind("userId", userId)
            .bind("language", language.name)
            .map { row -> row.get("source_word", String::class.java)!! }
            .all()
    }

    override fun findAllWordByTheirOrigins(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID,
    ): Flux<WordEntity> {
        val selectQuery = """
            SELECT w.id, w.type, w.source_word, w.translation, w.definition, w.extra_mark,
                   w.language, w.is_bookmarked, w.is_from_unverified_source, w.user_id, w.bank_id,
                   w.bank_group_id, w.created_at, w.updated_at
            FROM words w
                INNER JOIN word_progress wp ON wp.word_id = w.id AND wp.user_id = w.user_id
            WHERE w.language = :language
              AND w.source_word = ANY(:origins)
              AND w.user_id = :userId
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("origins", origins.toTypedArray())
            .bind("userId", userId)
            .map { row -> mapWordEntity(row) }
            .all()
    }

    override fun getWordsForGame(
        userId: UUID,
        language: LanguageName,
        completed: Boolean,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
    ): Mono<Set<String>> {
        val whereClause = buildList {
            add("words.language = :language")
            add("words.user_id = :userId")
            add("wp.id IS NOT NULL")
            if (completed) {
                add("wp.completed_at IS NOT NULL")
            } else {
                add("(wp.completed_at IS NULL)")
            }
            banksIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_id = ANY(:banksIds)") }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_group_id = ANY(:bankGroupsIds)") }
        }.joinToString(" AND ")

        val valuesBindings = mutableMapOf<String, Any>(
            "userId" to userId,
            "language" to language.name,
        ).apply {
            banksIds?.takeIf { it.isNotEmpty() }?.let { put("banksIds", it.toTypedArray()) }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { put("bankGroupsIds", it.toTypedArray()) }
        }

        val selectQuery = buildString {
            appendLine(
                """
                SELECT words.source_word
                FROM words
                    INNER JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id
                """.trimIndent(),
            )
            append("WHERE ")
            append(whereClause)
        }

        return databaseClient.sql(selectQuery)
            .bindValues(valuesBindings)
            .map { row -> row.get("source_word", String::class.java)!! }
            .all()
            .collectList()
            .map { it.toSet() }
    }

    override fun countCreated(language: LanguageName, userId: UUID): Mono<CountingSummary> {
        val query = """
            SELECT * FROM count_words_by_field(
                'created_at',
                cast(:language as text),
                :userId
            )
        """
        return handleCountQuery(query, language, userId)
    }

    override fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Mono<Int> {
        val query = """
            UPDATE words
            SET bank_id = :bankId
            WHERE id = :wordId AND user_id = :userId
        """
        return handleChangeBankQuery(query, userId, bankId, mapOf("wordId" to wordId))
    }

    override fun changeBankForMultipleWords(bankId: UUID?, wordIds: List<UUID>, userId: UUID): Mono<Int> {
        val updateQuery = """
            UPDATE words
            SET bank_id = :bankId
            WHERE id = ANY(:wordIds) AND user_id = :userId
        """
        return handleChangeBankQuery(updateQuery, userId, bankId, mapOf("wordIds" to wordIds.toTypedArray()))
    }

    private fun mapProgress(row: Readable): WordProgressDTO? {
        val pointsValue = row.get("wp_points") ?: return null
        val points = when (pointsValue) {
            is Int -> pointsValue
            is Long -> pointsValue.toInt()
            is Number -> pointsValue.toInt()
            else -> return null
        }
        val completedAt = row.get("wp_completed_at", Instant::class.java)
        val firstCompletedAt = row.get("wp_first_completed_at", Instant::class.java)
        val isCompleted = points >= GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD
        return WordProgressDTO(points, isCompleted, completedAt, firstCompletedAt)
    }

    private fun mapWordEntity(row: Readable): WordEntity {
        return WordEntity(
            id = row.get("id", UUID::class.java)!!,
            type = row.get("type", String::class.java)?.let { WordType.valueOf(it) },
            sourceWord = row.get("source_word", String::class.java)!!,
            translation = row.get("translation", String::class.java),
            definition = row.get("definition", String::class.java),
            extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
            language = LanguageName.valueOf(row.get("language", String::class.java)!!),
            isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
            isFromUnverifiedSource = row.get("is_from_unverified_source", Boolean::class.java)!!,
            userId = row.get("user_id", UUID::class.java)!!,
            bankId = row.get("bank_id", UUID::class.java),
            bankGroupId = row.get("bank_group_id", UUID::class.java),
            createdAt = row.get("created_at", Instant::class.java)!!,
            updatedAt = row.get("updated_at", Instant::class.java)!!,
        )
    }

    private fun mapSingleWordResponse(row: Readable): SingleWordResponse {
        return SingleWordResponse(
            id = row.get("id", UUID::class.java)!!,
            type = row.get("type", String::class.java)?.let { WordType.valueOf(it) },
            sourceWord = row.get("source_word", String::class.java)!!,
            translation = row.get("translation", String::class.java),
            definition = row.get("definition", String::class.java),
            extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
            language = LanguageName.valueOf(row.get("language", String::class.java)!!),
            isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
            isFromUnverifiedSource = row.get("is_from_unverified_source", Boolean::class.java)!!,
            progress = mapProgress(row),
            bank = BankCompact.construct(row),
            createdAt = row.get("created_at", Instant::class.java)!!,
            updatedAt = row.get("updated_at", Instant::class.java)!!,
        )
    }

    private fun mapWordListItem(row: Readable): WordListItem {
        return WordListItem(
            id = row.get("id", UUID::class.java)!!,
            sourceWord = row.get("source_word", String::class.java)!!,
            translation = row.get("translation", String::class.java),
            definition = row.get("definition", String::class.java),
            isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
            isFromUnverifiedSource = row.get("is_from_unverified_source", Boolean::class.java)!!,
            progress = mapProgress(row),
            type = row.get("type", String::class.java)?.let { WordType.valueOf(it) },
            extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
            language = LanguageName.valueOf(row.get("language", String::class.java)!!),
            bank = BankCompact.construct(row),
            createdAt = row.get("created_at", Instant::class.java)!!,
        )
    }

    private fun handleCountQuery(query: String, language: LanguageName, userId: UUID): Mono<CountingSummary> {
        return databaseClient.sql(query)
            .bind("language", language.name)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { row ->
                CountingSummary(
                    today = (row["today"] as? Number)?.toInt() ?: 0,
                    week = (row["week"] as? Number)?.toInt() ?: 0,
                    month = (row["month"] as? Number)?.toInt() ?: 0,
                )
            }
    }

    private fun handleChangeBankQuery(
        query: String,
        userId: UUID,
        bankId: UUID?,
        params: Map<String, Any>,
    ): Mono<Int> {
        val spec = databaseClient.sql(query)
            .bind("userId", userId)
            .bindValues(params)

        val finalSpec = if (bankId != null) spec.bind("bankId", bankId) else spec.bindNull("bankId", UUID::class.java)

        return finalSpec.fetch().rowsUpdated().map { it.toInt() }
    }

    private fun resolveWordsOrderByClause(
        sortBy: GetAllWordsSortOptions,
        sortDirection: SortDirection,
    ): String {
        return when (sortBy) {
            GetAllWordsSortOptions.CREATED_AT -> when (sortDirection) {
                SortDirection.ASC -> "ORDER BY words.created_at ASC"
                SortDirection.DESC -> "ORDER BY words.created_at DESC"
            }

            GetAllWordsSortOptions.SOURCE_WORD -> when (sortDirection) {
                SortDirection.ASC -> "ORDER BY words.source_word ASC"
                SortDirection.DESC -> "ORDER BY words.source_word DESC"
            }
        }
    }

    private fun createQueryConditions(
        isFromUnverifiedSource: Boolean?,
        hasProgress: Boolean?,
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordTypes: Set<WordType>?,
        wordExtraMarks: Set<WordExtraMark>?,
    ): String {
        return buildList {
            add("words.user_id = :userId")
            add("words.language = :language")
            isFromUnverifiedSource?.let { add("words.is_from_unverified_source = :isFromUnverifiedSource") }
            hasProgress?.let {
                if (it) add("wp.id IS NOT NULL") else add("wp.id IS NULL")
            }
            completed?.let {
                if (it) add("wp.completed_at IS NOT NULL") else add("(wp.completed_at IS NULL OR wp.id IS NULL)")
            }
            searchingPhrase?.let { add("words.source_word ILIKE :searchingPhrase") }
            bookmarked?.let { add("words.is_bookmarked = :bookmarked") }
            banksIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_id = ANY(:banksIds)") }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_group_id = ANY(:bankGroupsIds)") }
            wordTypes?.takeIf { it.isNotEmpty() }?.let { add("words.type = ANY(:wordTypes)") }
            wordExtraMarks?.takeIf { it.isNotEmpty() }?.let { add("words.extra_mark = ANY(:wordExtraMarks)") }
        }.joinToString(" AND ")
    }

    private fun createValuesBindings(
        userId: UUID,
        language: LanguageName,
        isFromUnverifiedSource: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordTypes: Set<WordType>?,
        wordExtraMarks: Set<WordExtraMark>?,
    ): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "userId" to userId,
            "language" to language.name,
        ).apply {
            isFromUnverifiedSource?.let { put("isFromUnverifiedSource", it) }
            searchingPhrase?.let { put("searchingPhrase", "%$it%") }
            bookmarked?.let { put("bookmarked", it) }
            wordTypes?.takeIf { it.isNotEmpty() }?.let { put("wordTypes", it.map { type -> type.name }.toTypedArray()) }
            wordExtraMarks?.takeIf { it.isNotEmpty() }?.let {
                put("wordExtraMarks", it.map { mark -> mark.name }.toTypedArray())
            }
            banksIds?.takeIf { it.isNotEmpty() }?.let { put("banksIds", it.toTypedArray()) }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { put("bankGroupsIds", it.toTypedArray()) }
        }
    }
}
