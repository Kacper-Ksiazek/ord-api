package com.backend.ord.repositories.impl

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.api.responses.words.embedded.BankCompact
import com.backend.ord.api.responses.words.embedded.BankGroupCompact
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.BankGroup
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.repositories.WordRepositoryCustomMethods
import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class WordRepositoryCustomMethodsImpl(
    private val entityManager: EntityManager
) : WordRepositoryCustomMethods {

    override fun findManyWords(
        searchingPhrase: String?,
        bookmarkedOnly: Boolean?,

        banksIds: List<UUID>?,
        bankGroupsIds: List<UUID>?,

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions,

        user: User,

        page: Int,
        perPage: Int
    ): List<WordAsGetManyWordResponse> {
        // ---
        // 1. Create CriteriaBuilder and CriteriaQuery
        // ---
        val criteriaBuilder = entityManager.criteriaBuilder
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
        val predicates = mutableListOf<Predicate>()

        // 3.1 Mandatory predicates
        predicates.add(criteriaBuilder.equal(root.get<UUID>("userId"), user.id))
        predicates.add(criteriaBuilder.equal(root.get<LanguageName>("translatedFrom"), language))

        // 3.2 Optional predicates

        // 3.2.1 - isBookmarked
        bookmarkedOnly?.let {
            predicates.add(criteriaBuilder.equal(root.get<Boolean>("isBookmarked"), it))
        }

        // 3.2.2 - wordType
        wordType?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordType>("type"), it))
        }

        // 3.2.3 - searchingPhrase
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

        // 3.2.4 - banksIds
        banksIds?.let {
            val bankIdPath = root.get<UUID>("bankId")
            predicates.add(bankIdPath.`in`(it))
        }

        // 3.2.5 - bankGroupsIds
        bankGroupsIds?.let {
            val bankGroupIdPath = bankJoin.get<UUID>("bankGroupId")
            predicates.add(bankGroupIdPath.`in`(it))
        }

        // 3.3 Apply predicates
        criteriaQuery.where(*predicates.toTypedArray())

        // ---
        // 4. Prepare sorting
        // ---
//        val order = if (sortDirection == SortDirection.DESC) {
//            criteriaBuilder.desc(root.get<Any>(sortBy.name))
//        } else {
//            criteriaBuilder.asc(root.get<Any>(sortBy.name))
//        }
//        criteriaQuery.orderBy(order)

        // ---
        // 5. Count total amount of results and calculate total amount of pages
        // ---
        // TODO: 5. Return the total amount of pages for the given pagination parameters

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
            root.get<WordType>("type"),                 // 5
            root.get<WordExtraMark?>("extraMark"),      // 6
            root.get<LanguageName>("translatedFrom"),   // 7
            root.get<LanguageName>("translatedTo"),     // 8
            root.get<UUID?>("bankId"),                  // 9
            // Bank fields
            bankJoin.get<UUID?>("id"),                  // 10
            bankJoin.get<String?>("name"),              // 11
            bankJoin.get<String?>("description"),       // 12
            // BankGroup fields
            bankGroupJoin.get<UUID?>("id"),             // 13
            bankGroupJoin.get<String?>("name"),         // 14
            bankGroupJoin.get<String?>("color"),        // 15
            // Timestamps
            root.get<Instant>("createdAt"),             // 16
            root.get<Instant>("updatedAt")              // 17
        )

        // 6.1 Execute query with pagination
        val query: TypedQuery<Tuple> = entityManager.createQuery(criteriaQuery)
            .setFirstResult(page * perPage)
            .setMaxResults(perPage)


        // 6.2 Map results to DTOs
        return query.resultList.map { tuple ->
            val id = tuple.get(0, UUID::class.java)
            val points = tuple.get(1, Int::class.java)
            val origin = tuple.get(2, String::class.java)
            val translation = tuple.get(3, String::class.java)
            val isBookmarked = tuple.get(4, Boolean::class.java)
            val type = tuple.get(5, WordType::class.java)
            val extraMark = tuple.get(6, WordExtraMark::class.java)
            val translatedFrom = tuple.get(7, LanguageName::class.java)
            val translatedTo = tuple.get(8, LanguageName::class.java)
            val bankId = tuple.get(9, UUID::class.java)
            // Bank fields
            val bankIdParam = tuple.get(10, UUID::class.java)
            val bankName = tuple.get(11, String::class.java)
            val bankDescription = tuple.get(12, String::class.java)
            // BankGroup fields
            val bankGroupId = tuple.get(13, UUID::class.java)
            val bankGroupName = tuple.get(14, String::class.java)
            val bankGroupColor = tuple.get(15, String::class.java)
            // Timestamps
            val createdAt = tuple.get(16, Instant::class.java)
            val updatedAt = tuple.get(17, Instant::class.java)

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
                translation = translation,
                isBookmarked = isBookmarked,
                type = type,
                extraMark = extraMark,
                translatedFrom = translatedFrom,
                translatedTo = translatedTo,
                bankId = bankId,
                bank = bankCompact,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

