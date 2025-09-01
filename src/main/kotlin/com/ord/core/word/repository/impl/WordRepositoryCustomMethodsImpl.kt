package com.ord.core.word.repository.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
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
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank_group.dto.BankGroupCompact
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.api.dto.responses.PaginationData
import com.ord.shared.domain.enums.SortDirection
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Repository
class WordRepositoryCustomMethodsImpl(
    private val databaseClient: DatabaseClient,
) : WordRepositoryCustomMethods {

    override fun findOneWord(
        wordId: UUID,
        user: UserEntity
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

        return databaseClient.sql(selectQuery)
            .bind("wordId", wordId)
            .bind("userId", user.id)
            .map { row ->
                val hasBank: Boolean = row.get("bank_name", String::class.java) != null
                val hasBankGroup: Boolean = hasBank && row.get("bank_group_name", String::class.java) != null

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

                    useCases = row.get("use_cases", Array<String>::class.java)?.toSet() ?: emptySet(),
                    exampleSentences = row.get("example_sentences", Array<ExampleSentence>::class.java)?.toSet() ?: emptySet(),

                    bank = if (hasBank) {
                        BankCompact(
                            name = row.get("bank_name", String::class.java)!!,
                            bankGroup = if (hasBankGroup) {
                                BankGroupCompact(
                                    name = row.get("bank_group_name", String::class.java)!!,
                                    color = row.get("bank_group_color", String::class.java)!!
                                )
                            } else null
                        )
                    } else null,

                    createdAt = row.get("created_at", Instant::class.java)!!,
                    updatedAt = row.get("updated_at", Instant::class.java)!!
                )
            }
            .one()
            .switchIfEmpty(
                Mono.error(NotFoundException("Word with id $wordId not found for user with id ${user.id}"))
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
        val filters =
            createFilters(completed, searchingPhrase, bookmarked, banksIds, bankGroupsIds, wordType, wordExtraMark)

        val countQuery = StringBuilder("SELECT COUNT(*) FROM words WHERE $filters")

        val selectQuery = StringBuilder(
            """
                SELECT 
                    ${WordListItem.fields.joinToString(", ") { "words.$it" }},
                    ${BankCompact.fields.joinToString(", ") { "banks.$it AS bank_$it" }},
                    ${BankGroupCompact.fields.joinToString(", ") { "bank_groups.$it AS bank_group_$it" }}
                FROM words
                    LEFT JOIN banks ON words.bank_id = banks.id
                    LEFT JOIN bank_groups ON banks.group_id = bank_groups.id
                WHERE $filters
                ORDER BY ${sortBy.column} ${sortDirection.name}
                LIMIT :limit OFFSET :offset 
            """
        )

        val countQueryResult: Mono<Long> = databaseClient.sql(countQuery.toString())
            .bind("language", language.name)
            .bind("userId", userId)
            .applyFilters(completed, searchingPhrase, bookmarked, banksIds, bankGroupsIds, wordType, wordExtraMark)
            .map { row -> row.get(0, Long::class.java)!! }
            .one()

        val selectQueryResult: Mono<List<WordListItem>> = databaseClient.sql(selectQuery.toString())
            .bind("language", language.name)
            .bind("userId", userId)
            .bind("limit", perPage)
            .bind("offset", (page - 1) * perPage)
            .map { row ->
                val hasBank: Boolean = row.get("bank_name", String::class.java) != null
                val hasBankGroup: Boolean = hasBank && row.get("bank_group_name", String::class.java) != null

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

                    bank = if (hasBank) {
                        BankCompact(
                            name = row.get("bank_name", String::class.java)!!,
                            bankGroup = if (hasBankGroup) {
                                BankGroupCompact(
                                    name = row.get("bank_group_name", String::class.java)!!,
                                    color = row.get("bank_group_color", String::class.java)!!
                                )
                            } else null
                        )
                    } else null,
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

    private fun createFilters(
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?
    ): String {
        return StringBuilder().apply {
            append("language = :language AND user_id = :userId")

            if (completed != null) append(" AND completed = :completed")
            if (searchingPhrase != null) append(" AND word ILIKE :searchingPhrase")
            if (bookmarked != null) append(" AND bookmarked = :bookmarked")
            if (!banksIds.isNullOrEmpty()) append(" AND bank_id = ANY(:banksIds)")
            if (!bankGroupsIds.isNullOrEmpty()) append(" AND bank_group_id = ANY(:bankGroupsIds)")
            if (wordType != null) append(" AND word_type = :wordType")
            if (wordExtraMark != null) append(" AND word_extra_mark = :wordExtraMark")
        }.toString()
    }


    private fun DatabaseClient.GenericExecuteSpec.applyFilters(
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        wordExtraMark: WordExtraMark?
    ): DatabaseClient.GenericExecuteSpec = this.apply {
        if (completed != null) bind("completed", completed)
        if (searchingPhrase != null) bind("searchingPhrase", "%$searchingPhrase%")
        if (bookmarked != null) bind("bookmarked", bookmarked)
        if (!banksIds.isNullOrEmpty()) bind("banksIds", banksIds.toTypedArray())
        if (!bankGroupsIds.isNullOrEmpty()) bind("bankGroupsIds", bankGroupsIds.toTypedArray())
        if (wordType != null) bind("wordType", wordType.name)
        if (wordExtraMark != null) bind("wordExtraMark", wordExtraMark.name)
    }
}