@file:Suppress("UNCHECKED_CAST")

package com.backend.ord.repositories.impl

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.enums.isDesc
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.requests.word.enums.toSQLColumnName
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.PaginationData
import com.backend.ord.api.responses.words.SingleWordResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.api.responses.words.embedded.BankCompact
import com.backend.ord.api.responses.words.embedded.BankGroupCompact
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.BankGroup
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.repositories.WordRepositoryCustomMethods
import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class WordRepositoryCustomMethodsImpl(
    private val entityManager: EntityManager
) : WordRepositoryCustomMethods {
    private val criteriaBuilder = entityManager.criteriaBuilder

    override fun findOneWord(
        wordId: UUID,
        user: User
    ): SingleWordResponse {
        val criteriaQuery = criteriaBuilder.createTupleQuery()

        val root = criteriaQuery.from(Word::class.java)
        val bankJoin = root.join<Word, Bank>("bank", JoinType.LEFT)
        val bankGroupJoin = bankJoin.join<Bank, BankGroup>("bankGroup", JoinType.LEFT)

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
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarkedOnly: Boolean?,

        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions,

        user: User,

        page: Int,
        perPage: Int
    ): PaginatedDataResponse<WordAsGetManyWordResponse> {
        // ---
        // 1. Create and CriteriaQuery
        // ---
        val criteriaQuery = criteriaBuilder.createTupleQuery()

        // ---
        // 2. Prepare root and joins
        // ---
        val root = criteriaQuery.from(Word::class.java)
        val bankJoin = root.join<Word, Bank>("bank", JoinType.LEFT)
        val bankGroupJoin = bankJoin.join<Bank, BankGroup>("bankGroup", JoinType.LEFT)

        // ---
        // 3. Prepare predicates
        // ---
        applyPredicatesToQuery<Tuple>(
            query = criteriaQuery,
            root = root,

            userId = user.id,
            language = language,

            wordType = wordType,
            completed = completed,
            wordExtraMark = wordExtraMark,
            bookmarkedOnly = bookmarkedOnly,
            searchingPhrase = searchingPhrase,

            banksIds = banksIds,
            bankGroupsIds = bankGroupsIds
        )

        // ---
        // 4. Prepare sorting
        // ---
        val sortByColumn = sortBy.toSQLColumnName()

        if (sortDirection.isDesc()) {
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get<Any>(sortByColumn)))
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get<Any>(sortByColumn)))
        }

        // ---
        // 5. Count total amount of results and calculate total amount of pages
        // ---
        val countCriteriaQuery = criteriaBuilder.createQuery(Long::class.java)
        val countRoot = countCriteriaQuery.from(Word::class.java)
        countCriteriaQuery.select(criteriaBuilder.count(countRoot))

        applyPredicatesToQuery<Long>(
            query = countCriteriaQuery,
            root = countRoot,

            userId = user.id,
            language = language,

            wordType = wordType,
            banksIds = banksIds,
            completed = completed,
            wordExtraMark = wordExtraMark,
            bookmarkedOnly = bookmarkedOnly,
            searchingPhrase = searchingPhrase,

            bankGroupsIds = bankGroupsIds
        )

        val totalRecords: Long = entityManager.createQuery(countCriteriaQuery).singleResult

        // ---
        // 6. Select fields using multiselect
        // ---
        criteriaQuery.multiselect(
            // Word fields
            root.get<UUID>("id"),                       // 0
            root.get<Int>("points"),                    // 1
            root.get<String>("origin"),                 // 2
            root.get<String>("translation"),            // 3
            root.get<Boolean>("isBookmarked"),          // 4
            root.get<Boolean>("isCompleted"),           // 5
            root.get<WordType>("type"),                 // 6
            root.get<WordExtraMark?>("extraMark"),      // 7
            root.get<LanguageName>("translatedFrom"),   // 8
            root.get<LanguageName>("translatedTo"),     // 9
            root.get<UUID?>("bankId"),                  // 10
            // Bank fields
            bankJoin.get<UUID?>("id"),                  // 11
            bankJoin.get<String?>("name"),              // 12
            bankJoin.get<String?>("description"),       // 13
            // BankGroup fields
            bankGroupJoin.get<UUID?>("id"),             // 14
            bankGroupJoin.get<String?>("name"),         // 15
            bankGroupJoin.get<String?>("color"),        // 16
            // Timestamps
            root.get<Instant>("createdAt"),             // 17
            root.get<Instant>("updatedAt")              // 18
        )

        // 6.1 Execute query with pagination
        val query: TypedQuery<Tuple> = entityManager.createQuery(criteriaQuery)
            .setFirstResult(page * perPage)
            .setMaxResults(perPage)


        // 6.2 Map results to DTOs
        val responseData = query.resultList.map { tuple ->
            val id = tuple.get(0, UUID::class.java)
            val points = tuple.get(1, Int::class.java)
            val origin = tuple.get(2, String::class.java)
            val translation = tuple.get(3, String::class.java)
            val isBookmarked = tuple.get(4, Boolean::class.java)
            val isCompleted = tuple.get(5, Boolean::class.java)
            val type = tuple.get(6, WordType::class.java)
            val extraMark = tuple.get(7, WordExtraMark::class.java)
            val translatedFrom = tuple.get(8, LanguageName::class.java)
            val translatedTo = tuple.get(9, LanguageName::class.java)
            val bankId = tuple.get(10, UUID::class.java)
            // Bank fields
            val bankIdParam = tuple.get(11, UUID::class.java)
            val bankName = tuple.get(12, String::class.java)
            val bankDescription = tuple.get(13, String::class.java)
            // BankGroup fields
            val bankGroupId = tuple.get(14, UUID::class.java)
            val bankGroupName = tuple.get(15, String::class.java)
            val bankGroupColor = tuple.get(16, String::class.java)
            // Timestamps
            val createdAt = tuple.get(17, Instant::class.java)
            val updatedAt = tuple.get(18, Instant::class.java)

            // Construct BankGroupCompact
            val bankGroupCompact = if (bankGroupId != null) {
                BankGroupCompact(
                    id = bankGroupId,
                    name = bankGroupName ?: "",
                    color = bankGroupColor ?: ""
                )
            } else null

            // Construct BankCompact
            val bankCompact = if (bankIdParam != null) {
                BankCompact(
                    id = bankIdParam,
                    name = bankName ?: "",
                    description = bankDescription ?: "",
                    bankGroup = bankGroupCompact
                )
            } else null

            WordAsGetManyWordResponse(
                id = id,

                points = points,
                origin = origin,
                isCompleted = isCompleted,
                translation = translation,
                isBookmarked = isBookmarked,

                type = type,
                extraMark = extraMark,
                translatedTo = translatedTo,
                translatedFrom = translatedFrom,

                bank = bankCompact,

                bankId = bankId,

                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        return PaginatedDataResponse(
            data = responseData,
            pagination = PaginationData(
                page = page,
                perPage = perPage,
                totalRecords = totalRecords,
                recordsOnCurrentPage = responseData.size
            )
        )
    }

    private fun <T> applyPredicatesToQuery(
        query: CriteriaQuery<T>,
        root: Root<Word>,

        userId: UUID,
        language: LanguageName,

        wordType: WordType?,
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarkedOnly: Boolean?,

        wordExtraMark: WordExtraMark?,

        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
    ) {
        val predicates = mutableListOf<Predicate>()

        // 1. Mandatory predicates
        predicates.add(criteriaBuilder.equal(root.get<UUID>("userId"), userId))
        predicates.add(criteriaBuilder.equal(root.get<LanguageName>("translatedFrom"), language))

        // 2 Optional predicates

        // 2.1 - isBookmarked
        bookmarkedOnly?.let {
            if (bookmarkedOnly == true) {
                predicates.add(criteriaBuilder.isTrue(root.get<Boolean>("isBookmarked")))
            }
        }

        // 2.2 - completed
        completed?.let {
            predicates.add(criteriaBuilder.equal(root.get<Boolean>("isCompleted"), completed))
        }

        // 2.3 - wordType
        wordType?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordType>("type"), it))
        }

        // 2.4 - wordExtraMark
        wordExtraMark?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordExtraMark>("extraMark"), it))
        }

        // 2.5 - searchingPhrase
        searchingPhrase?.let {
            predicates.add(
                criteriaBuilder.or(
                    // Search by origin - in learning language
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get<String>("origin")),
                        "%${it.lowercase()}%"
                    ),
                    // Search by translation - in translated to ( native ) language
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get<String>("translation")),
                        "%${it.lowercase()}%"
                    )
                )
            )
        }

        // 2.6 - banksIds
        banksIds?.let {
            val bankIdPath = root.get<UUID>("bankId")
            predicates.add(bankIdPath.`in`(it))
        }

        // 2.7 - bankGroupsIds
        bankGroupsIds?.let {
            val bankGroupIdPath = root.get<UUID>("bankGroupId")
            predicates.add(bankGroupIdPath.`in`(it))
        }

        query.where(*predicates.toTypedArray())
    }
}

