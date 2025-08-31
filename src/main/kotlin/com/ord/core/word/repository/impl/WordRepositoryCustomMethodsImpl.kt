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
import jakarta.persistence.Tuple
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.JoinType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Repository
class WordRepositoryCustomMethodsImpl(
    private val databaseClient: DatabaseClient,
) : WordRepositoryCustomMethods {
    private val criteriaBuilder = entityManager.criteriaBuilder

    override fun findOneWord(
        wordId: UUID,
        user: UserEntity
    ): SingleWordResponse {
        val criteriaQuery = criteriaBuilder.createTupleQuery()

        val root = criteriaQuery.from(WordEntity::class.java)
        val bankJoin = root.join<WordEntity, BankEntity>("bank", JoinType.LEFT)
        val bankGroupJoin = bankJoin.join<BankEntity, BankGroupEntity>("bankGroup", JoinType.LEFT)

        criteriaQuery.multiselect(
            root.get<UUID>("id"),                       // 0

            // Primitive type fields
            root.get<Int>("points"),                    // 1
            root.get<String>("origin"),                 // 2
            root.get<String>("translation"),            // 3
            root.get<String>("definition"),             // 4
            root.get<Boolean>("isBookmarked"),          // 5
            root.get<Boolean>("isCompleted"),           // 6

            // Enum types
            root.get<WordType>("type"),                 // 7
            root.get<WordExtraMark?>("extraMark"),      // 8
            root.get<LanguageName>("translatedTo"),     // 9
            root.get<LanguageName>("translatedFrom"),   // 10

            // Lists fields
            root.get<String>("useCases"),               // 11
            root.get<Int>("exampleSentences"),          // 12

            // Bank fields
            bankJoin.get<UUID?>("id"),                  // 13
            bankJoin.get<String?>("name"),              // 14
            bankJoin.get<String?>("description"),       // 15

            // BankGroup fields
            bankGroupJoin.get<UUID?>("id"),             // 16
            bankGroupJoin.get<String?>("name"),         // 17
            bankGroupJoin.get<String?>("color"),        // 18

            // Timestamps
            root.get<Instant>("createdAt"),             // 19
            root.get<Instant>("updatedAt")              // 20
        )

        criteriaQuery.where(
            criteriaBuilder.equal(root.get<UUID>("id"), wordId),
            criteriaBuilder.equal(root.get<UUID>("userId"), user.id)
        )

        val query: TypedQuery<Tuple> = entityManager.createQuery(criteriaQuery)

        if (query.resultList.isEmpty()) {
            throw NotFoundException("Word with id $wordId not found for user with id ${user.id}")
        }

        val result: Tuple = query.singleResult!!

        val bankId = result.get(13, UUID::class.java)
        val bankGroupId = result.get(16, UUID::class.java)

        @Suppress("UNCHECKED_CAST")
        return SingleWordResponse(
            id = result.get(0, UUID::class.java),

            points = result.get(1, Int::class.java),
            origin = result.get(2, String::class.java),
            translation = result.get(3, String::class.java),
            definition = result.get(4, String::class.java),
            isBookmarked = result.get(5, Boolean::class.java),
            isCompleted = result.get(6, Boolean::class.java),

            type = result.get(7, WordType::class.java),
            extraMark = result.get(8, WordExtraMark::class.java),
            translatedTo = result.get(9, LanguageName::class.java),
            translatedFrom = result.get(10, LanguageName::class.java),

            useCases = result.get(11, Set::class.java) as Set<String>,
            exampleSentences = result.get(12, Set::class.java) as Set<ExampleSentence>,

            bank = if (bankId != null) {
                BankCompact(
                    id = bankId,
                    name = result.get(14, String::class.java) ?: "",
                    description = result.get(15, String::class.java) ?: "",
                    bankGroup = if (bankGroupId != null) {
                        BankGroupCompact(
                            id = bankGroupId,
                            name = result.get(17, String::class.java) ?: "",
                            color = result.get(18, String::class.java) ?: ""
                        )
                    } else null
                )
            } else null,

            createdAt = result.get(19, Instant::class.java),
            updatedAt = result.get(20, Instant::class.java)
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