package com.ord.core.word.repositories.impl

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.crud.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.core.word.repositories.WordRepositoryCustomMethods
import com.ord.core.word.repositories.WordStatusCounts
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
        status: WordStatus?,
        completed: Boolean?,
        bookmarked: Boolean?,
        searchingPhrase: String?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?,
        sortDirection: SortDirection,
        sortBy: GetAllWordsSortOptions,
        page: Int,
        perPage: Int,
    ): Mono<WordsPaginatedResult> {
        val conditions = createQueryConditions(
            language, status, completed, searchingPhrase, bookmarked, banksIds, bankGroupsIds, wordType, wordExtraMark,
        )
        val valuesBindings = createValuesBindings(
            userId, language, status, completed, searchingPhrase, bookmarked, banksIds, bankGroupsIds, wordType, wordExtraMark,
        )

        val countQuery = """
            SELECT COUNT(*)
            FROM words
                LEFT JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id
            WHERE $conditions
        """

        val selectQuery = """
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
            WHERE $conditions
            ORDER BY words.${sortBy.column} ${sortDirection.name}
            LIMIT :limit OFFSET :offset
        """

        val capturedCountQuery = """
            SELECT COUNT(*)
            FROM words
            WHERE words.user_id = :userId
              AND words.status = 'CAPTURED'
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

        fun toResult(words: List<WordListItem>, totalItems: Long, capturedCount: Long?) = WordsPaginatedResult(
            paginated = PaginatedDataResponse(
                data = words,
                pagination = PaginationData(
                    page = page,
                    perPage = perPage,
                    totalResults = totalItems,
                    resultsOnCurrentPage = words.size,
                ),
            ),
            capturedCount = capturedCount,
        )

        return if (status == null) {
            val capturedCountResult = databaseClient.sql(capturedCountQuery)
                .bind("userId", userId)
                .bind("language", language.name)
                .map { row -> row.get(0, Long::class.java)!! }
                .one()

            Mono.zip(selectQueryResult, countQueryResult, capturedCountResult)
                .map { t -> toResult(t.t1, t.t2, t.t3) }
        } else {
            Mono.zip(selectQueryResult, countQueryResult)
                .map { t -> toResult(t.t1, t.t2, capturedCount = null) }
        }
    }

    override fun countByStatus(userId: UUID): Mono<WordStatusCounts> {
        val query = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS active_count,
                COALESCE(SUM(CASE WHEN status = 'CAPTURED' THEN 1 ELSE 0 END), 0) AS captured_count
            FROM words
            WHERE user_id = :userId
        """

        return databaseClient.sql(query)
            .bind("userId", userId)
            .map { row ->
                WordStatusCounts(
                    total = row.get("total", Long::class.java)!!,
                    activeCount = row.get("active_count", Long::class.java)!!,
                    capturedCount = row.get("captured_count", Long::class.java)!!,
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
              AND w.status = 'ACTIVE'
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
            SELECT id, status, type, source_word, translation, definition, extra_mark,
                   language, is_bookmarked, user_id, bank_id, bank_group_id, created_at, updated_at
            FROM words
            WHERE language = :language
              AND source_word = ANY(:origins)
              AND user_id = :userId
              AND status = 'ACTIVE'
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("origins", origins.toTypedArray())
            .bind("userId", userId)
            .map { row ->
                WordEntity(
                    id = row.get("id", UUID::class.java)!!,
                    status = WordStatus.valueOf(row.get("status", String::class.java)!!),
                    type = row.get("type", String::class.java)?.let { WordType.valueOf(it) },
                    sourceWord = row.get("source_word", String::class.java)!!,
                    translation = row.get("translation", String::class.java),
                    definition = row.get("definition", String::class.java),
                    extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
                    language = LanguageName.valueOf(row.get("language", String::class.java)!!),
                    isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
                    userId = userId,
                    bankId = row.get("bank_id", UUID::class.java),
                    bankGroupId = row.get("bank_group_id", UUID::class.java),
                    createdAt = row.get("created_at", Instant::class.java)!!,
                    updatedAt = row.get("updated_at", Instant::class.java)!!,
                )
            }
            .all()
    }

    override fun getWordsForGame(
        userId: UUID,
        language: LanguageName,
        completed: Boolean,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
    ): Mono<Set<String>> {
        val criterias = buildList {
            add("words.language = :language")
            add("words.user_id = :userId")
            add("words.status = 'ACTIVE'")
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

        val selectQuery = """
            SELECT words.source_word
            FROM words
                INNER JOIN word_progress wp ON wp.word_id = words.id AND wp.user_id = words.user_id
            WHERE $criterias
        """

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

    private fun mapSingleWordResponse(row: Readable): SingleWordResponse {
        return SingleWordResponse(
            id = row.get("id", UUID::class.java)!!,
            status = WordStatus.valueOf(row.get("status", String::class.java)!!),
            type = row.get("type", String::class.java)?.let { WordType.valueOf(it) },
            sourceWord = row.get("source_word", String::class.java)!!,
            translation = row.get("translation", String::class.java),
            definition = row.get("definition", String::class.java),
            extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
            language = LanguageName.valueOf(row.get("language", String::class.java)!!),
            isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
            progress = mapProgress(row),
            bank = BankCompact.construct(row),
            createdAt = row.get("created_at", Instant::class.java)!!,
            updatedAt = row.get("updated_at", Instant::class.java)!!,
        )
    }

    private fun mapWordListItem(row: Readable): WordListItem {
        return WordListItem(
            id = row.get("id", UUID::class.java)!!,
            status = WordStatus.valueOf(row.get("status", String::class.java)!!),
            sourceWord = row.get("source_word", String::class.java)!!,
            translation = row.get("translation", String::class.java),
            isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
            progress = mapProgress(row),
            type = row.get("type", String::class.java)?.let { WordType.valueOf(it) },
            extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
            language = LanguageName.valueOf(row.get("language", String::class.java)!!),
            bank = BankCompact.construct(row),
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

    private fun createQueryConditions(
        language: LanguageName,
        status: WordStatus?,
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?,
    ): String {
        return buildList {
            add("words.user_id = :userId")
            add("words.language = :language")
            status?.let { add("words.status = CAST(:status AS word_status)") }
            completed?.let {
                if (it) add("wp.completed_at IS NOT NULL") else add("(wp.completed_at IS NULL OR words.status = 'CAPTURED')")
            }
            searchingPhrase?.let { add("words.source_word ILIKE :searchingPhrase") }
            bookmarked?.let { add("words.is_bookmarked = :bookmarked") }
            banksIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_id = ANY(:banksIds)") }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_group_id = ANY(:bankGroupsIds)") }
            wordType?.let { add("words.type = :wordType") }
            wordExtraMark?.let { add("words.extra_mark = :wordExtraMark") }
        }.joinToString(" AND ")
    }

    private fun createValuesBindings(
        userId: UUID,
        language: LanguageName,
        status: WordStatus?,
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?,
    ): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "userId" to userId,
            "language" to language.name,
        ).apply {
            status?.let { put("status", it.name) }
            searchingPhrase?.let { put("searchingPhrase", "%$it%") }
            bookmarked?.let { put("bookmarked", it) }
            wordType?.let { put("wordType", it.name) }
            wordExtraMark?.let { put("wordExtraMark", it.name) }
            banksIds?.takeIf { it.isNotEmpty() }?.let { put("banksIds", it.toTypedArray()) }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { put("bankGroupsIds", it.toTypedArray()) }
        }
    }
}
