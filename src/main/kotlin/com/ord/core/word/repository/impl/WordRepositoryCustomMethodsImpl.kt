package com.ord.core.word.repository.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.core.word.repository.WordRepositoryCustomMethods
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.bank.dto.BankCompact
import com.ord.features.bank_group.dto.BankGroupCompact
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.api.dto.responses.PaginationData
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.domain.dto.CountingSummary
import io.r2dbc.postgresql.codec.Json
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Repository
class WordRepositoryCustomMethodsImpl(
    template: R2dbcEntityTemplate
) : WordRepositoryCustomMethods {
    private val databaseClient: DatabaseClient = template.databaseClient
    private val objectMapper = jacksonObjectMapper()

    override fun findOneWord(
        wordId: UUID,
        userId: UUID,
    ): Mono<SingleWordResponse> {
        val selectQuery = """
            SELECT 
                ${SingleWordResponse.fields.joinToString(", ") { "words.$it" }},
                ${BankCompact.fields.joinToString(", ") { "banks.$it AS bank_$it" }},
                ${BankGroupCompact.fields.joinToString(", ") { "bank_groups.$it AS bank_group_$it" }}
            FROM words
                LEFT JOIN banks ON words.bank_id = banks.id
                LEFT JOIN bank_groups ON banks.group_id = bank_groups.id
            WHERE words.id = :wordId AND words.user_id = :userId
        """

        return databaseClient
            .sql(selectQuery)
            .bind("wordId", wordId)
            .bind("userId", userId)
            .map { row ->
                SingleWordResponse(
                    id = row.get("id", UUID::class.java)!!,
                    points = row.get("points", Int::class.java)!!,
                    origin = row.get("origin", String::class.java)!!,
                    translation = row.get("translation", String::class.java)!!,
                    definition = row.get("definition", String::class.java) ?: "",
                    isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
                    isCompleted = row.get("is_completed", Boolean::class.java)!!,

                    type = WordType.valueOf(row.get("type", String::class.java)!!),
                    extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
                    translatedTo = LanguageName.valueOf(row.get("translated_to", String::class.java)!!),
                    translatedFrom = LanguageName.valueOf(row.get("translated_from", String::class.java)!!),

                    useCases = objectMapper.readValue(
                        row.get("use_cases", String::class.java) ?: "[]",
                        object : TypeReference<Set<String>>() {}
                    ),
                    exampleSentences = objectMapper.readValue(
                        row.get("example_sentences", String::class.java) ?: "[]",
                        object : TypeReference<Set<ExampleSentence>>() {}
                    ),

                    bank = BankCompact.construct(row),

                    createdAt = row.get("created_at", Instant::class.java)!!,
                    updatedAt = row.get("updated_at", Instant::class.java)!!
                )
            }
            .one()
            .switchIfEmpty(
                Mono.error(NotFoundException("Word with id $wordId not found for user with id $userId"))
            )
    }


    override fun findNOfLatestWords(
        userId: UUID,
        language: LanguageName,
        limit: Int
    ): Flux<String> {
        val selectQuery = """
            SELECT origin 
            FROM words 
            WHERE 
                translated_from = :language 
                AND user_id = :userId
            ORDER BY created_at DESC 
            LIMIT :limit
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row -> row.get("origin", String::class.java)!! }
            .all()
    }


    override fun findNOfMostDifficultWords(
        userId: UUID,
        language: LanguageName,
        limit: Int
    ): Flux<String> {
        val selectQuery = """
            SELECT origin 
            FROM words 
            WHERE 
                translated_from = :language 
                AND user_id = :userId
            ORDER BY points DESC 
            LIMIT :limit
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row -> row.get("origin", String::class.java)!! }
            .all()
    }


    override fun findAllWordsFromBanks(
        userId: UUID,
        language: LanguageName,
        banksIds: List<UUID>
    ): Flux<String> {
        val selectQuery = """
            SELECT origin 
            FROM words 
            WHERE 
                translated_from = :language 
                AND user_id = :userId
                AND bank_id = ANY(:banksIds)
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("banksIds", banksIds.toTypedArray())
            .map { row -> row.get("origin", String::class.java)!! }
            .all()
    }


    override fun findAllWordByTheirOrigins(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID
    ): Flux<WordEntity> {
        val selectQuery = """
            SELECT id, origin, translation, definition, points, is_bookmarked, is_completed,
                   type, extra_mark, translated_to, translated_from, use_cases, example_sentences,
                   bank_id, user_id, created_at, updated_at
            FROM words 
            WHERE translated_from = :language 
            AND origin = ANY(:origins) 
            AND user_id = :userId
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("origins", origins.toTypedArray())
            .bind("userId", userId)
            .map { row ->
                WordEntity(
                    id = row.get("id", UUID::class.java)!!,
                    origin = row.get("origin", String::class.java)!!,
                    translation = row.get("translation", String::class.java)!!,
                    definition = row.get("definition", String::class.java) ?: "",
                    points = row.get("points", Int::class.java)!!,
                    isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
                    isCompleted = row.get("is_completed", Boolean::class.java)!!,
                    type = WordType.valueOf(row.get("type", String::class.java)!!),
                    extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
                    translatedTo = LanguageName.valueOf(row.get("translated_to", String::class.java)!!),
                    translatedFrom = LanguageName.valueOf(row.get("translated_from", String::class.java)!!),
                    useCases = row.get("use_cases", Json::class.java)!!,
                    exampleSentences = row.get("example_sentences", Json::class.java)!!,
                    userId = userId,
                    createdAt = row.get("created_at", Instant::class.java)!!,
                    updatedAt = row.get("updated_at", Instant::class.java)!!
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
            add("words.translated_from = :language")
            add("words.user_id = :userId")
            add("words.is_completed = :completed")

            banksIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_id = ANY(:banksIds)") }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_group_id = ANY(:bankGroupsIds)") }
        }.joinToString(" AND ")

        val valuesBindings = mutableMapOf<String, Any>(
            "userId" to userId,
            "language" to language.name,
            "completed" to completed
        ).apply {
            banksIds?.takeIf { it.isNotEmpty() }?.let { put("banksIds", it.toTypedArray()) }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { put("bankGroupsIds", it.toTypedArray()) }
        }

        // language=SQL
        val selectQuery = """
            SELECT origin 
            FROM words 
            WHERE $criterias
        """

        return databaseClient.sql(selectQuery)
            .bindValues(valuesBindings)
            .map { row -> row.get("origin", String::class.java)!! }
            .all()
            .collectList()
            .map { it.toSet() }
    }


    override fun countCreated(
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary> {
        // language=SQL
        val query = """
            SELECT * FROM count_words_by_field(
                'created_at', 
                cast(:language as text), 
                :userId
            )
        """

        return handleCountQuery(query, language, userId)
    }


    override fun countCompleted(
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary> {
        // language=SQL
        val query = """
            SELECT * FROM count_words_by_field(
                'completed_at', 
                cast(:language as text), 
                :userId
            )
        """

        return handleCountQuery(query, language, userId)
    }


    override fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Mono<Int> {
        // language=SQL
        val query = """
            UPDATE words 
            SET bank_id = :bankId 
            WHERE id = :wordId AND user_id = :userId
        """

        return handleChangeBankQuery(
            query = query,
            userId = userId,
            bankId = bankId,
            params = mapOf("wordId" to wordId)
        )
    }


    override fun changeBankForMultipleWords(
        bankId: UUID?,
        wordIds: List<UUID>,
        userId: UUID
    ): Mono<Int> {
        // language=SQL
        val updateQuery = """
            UPDATE words 
            SET bank_id = :bankId 
            WHERE id = ANY(:wordIds) AND user_id = :userId
        """

        return handleChangeBankQuery(
            query = updateQuery,
            userId = userId,
            bankId = bankId,
            params = mapOf("wordIds" to wordIds.toTypedArray())
        )
    }

    override fun findManyWords(
        userId: UUID,
        language: LanguageName,
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
        perPage: Int
    ): Mono<PaginatedDataResponse<WordListItem>> {
        val conditions =
            createQueryConditions(
                completed,
                searchingPhrase,
                bookmarked,
                banksIds,
                bankGroupsIds,
                wordType,
                wordExtraMark
            )

        val valuesBindings = createValuesBindings(
            userId,
            language,
            completed,
            searchingPhrase,
            bookmarked,
            banksIds,
            bankGroupsIds,
            wordType,
            wordExtraMark
        )

        val countQuery = StringBuilder(
            // language=SQL
            """
               SELECT 
                   COUNT(*)
               FROM words 
               WHERE $conditions 
            """
        )

        val selectQuery = StringBuilder(
            """
                SELECT 
                    ${WordListItem.fields.joinToString(", ") { "words.$it" }},
                    ${BankCompact.fields.joinToString(", ") { "banks.$it AS bank_$it" }},
                    ${BankGroupCompact.fields.joinToString(", ") { "bank_groups.$it AS bank_group_$it" }}
                FROM words
                    LEFT JOIN banks ON words.bank_id = banks.id
                    LEFT JOIN bank_groups ON banks.group_id = bank_groups.id
                WHERE $conditions
                
                ORDER BY ${sortBy.column} ${sortDirection.name}
                LIMIT :limit OFFSET :offset 
            """
        )

        val countQueryResult: Mono<Long> = databaseClient.sql(countQuery.toString())
            .bindValues(valuesBindings)
            .map { row -> row.get(0, Long::class.java)!! }
            .one()

        val selectQueryResult: Mono<List<WordListItem>> = databaseClient
            .sql(selectQuery.toString())
            .bindValues(valuesBindings)
            .bind("limit", perPage)
            .bind("offset", maxOf(0, (page - 1) * perPage))
            .map { row ->
                WordListItem(
                    id = row.get("id", UUID::class.java)!!,

                    points = row.get("points", Int::class.java)!!,
                    origin = row.get("origin", String::class.java)!!,
                    translation = row.get("translation", String::class.java)!!,
                    isCompleted = row.get("is_completed", Boolean::class.java)!!,
                    isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,

                    type = WordType.valueOf(row.get("type", String::class.java)!!),
                    extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
                    translatedTo = LanguageName.valueOf(row.get("translated_to", String::class.java)!!),
                    translatedFrom = LanguageName.valueOf(row.get("translated_from", String::class.java)!!),

                    bank = BankCompact.construct(row),
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
                        page = page,
                        perPage = perPage,
                        totalResults = totalItems,
                        resultsOnCurrentPage = words.size
                    )
                )
            }
    }


    private fun handleCountQuery(
        query: String,
        language: LanguageName,
        userId: UUID
    ): Mono<CountingSummary> {
        return databaseClient.sql(query)
            .bind("language", language.name)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { row ->
                CountingSummary(
                    today = (row["today"] as? Number)?.toInt() ?: 0,
                    week = (row["week"] as? Number)?.toInt() ?: 0,
                    month = (row["month"] as? Number)?.toInt() ?: 0
                )
            }
    }


    private fun handleChangeBankQuery(
        query: String,
        userId: UUID,
        bankId: UUID?,
        params: Map<String, Any>
    ): Mono<Int> {
        val spec = databaseClient.sql(query)
            .bind("userId", userId)
            .bindValues(params)


        val finalSpec = if (bankId != null) {
            spec.bind("bankId", bankId)
        } else {
            spec.bindNull("bankId", UUID::class.java)
        }

        return finalSpec.fetch()
            .rowsUpdated()
            .map { it.toInt() }
    }


    private fun createQueryConditions(
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?
    ): String {
        val conditions = buildList {
            add("words.translated_from = :language")
            add("words.user_id = :userId")

            completed?.let { add("words.is_completed = :completed") }
            searchingPhrase?.let { add("words.origin ILIKE :searchingPhrase") }
            bookmarked?.let { add("words.is_bookmarked = :bookmarked") }
            banksIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_id = ANY(:banksIds)") }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { add("words.bank_group_id = ANY(:bankGroupsIds)") }
            wordType?.let { add("words.type = :wordType") }
            wordExtraMark?.let { add("words.extra_mark = :wordExtraMark") }
        }

        return conditions.joinToString(" AND ")
    }


    private fun createValuesBindings(
        userId: UUID,
        language: LanguageName,
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?
    ): Map<String, Any> {
        val params: Map<String, Any> = mutableMapOf<String, Any>(
            "userId" to userId,
            "language" to language.name
        ).apply {
            completed?.let { put("completed", it) }
            searchingPhrase?.let { put("searchingPhrase", "%$it%") }
            bookmarked?.let { put("bookmarked", it) }
            wordType?.let { put("wordType", it.name) }
            wordExtraMark?.let { put("wordExtraMark", it.name) }

            banksIds?.takeIf { it.isNotEmpty() }?.let { put("banksIds", it.toTypedArray()) }
            bankGroupsIds?.takeIf { it.isNotEmpty() }?.let { put("bankGroupsIds", it.toTypedArray()) }
        }

        return params.toMap()

    }

}