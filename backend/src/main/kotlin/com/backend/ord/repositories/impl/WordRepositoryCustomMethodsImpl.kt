package com.backend.ord.repositories.impl

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.domain.dto.BankDTO
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.repositories.WordRepositoryCustomMethods
import jakarta.persistence.EntityManager
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

        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions,

        user: User,

        page: Int,
        perPage: Int
    ): List<WordAsGetManyWordResponse> {

        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(WordAsGetManyWordResponse::class.java)
        val root = criteriaQuery.from(Word::class.java)

        val predicates = mutableListOf<Predicate>()

        // Mandatory predicates
        predicates.add(criteriaBuilder.equal(root.get<LanguageName>("translatedFrom"), language))
        predicates.add(criteriaBuilder.equal(root.get<UUID>("userId"), user.id))

        // Optional predicates
        wordType?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordType>("type"), it))
        }
        wordExtraMark?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordExtraMark>("extraMark"), it))
        }
        if (bookmarkedOnly == true) {
            predicates.add(criteriaBuilder.isTrue(root.get<Boolean>("isBookmarked")))
        }
        banksIds?.takeIf { it.isNotEmpty() }?.let {
            predicates.add(root.get<UUID>("bankId").`in`(it))
        }

        // Apply predicates to the query
        criteriaQuery.where(*predicates.toTypedArray())

        // Sorting based on the sort direction and field
//        val order = if (sortDirection == SortDirection.DESC) {
//            criteriaBuilder.desc(root.get<Any>(sortBy.name))
//        } else {
//            criteriaBuilder.asc(root.get<Any>(sortBy.name))
//        }
//        criteriaQuery.orderBy(order)

        // Select only the necessary fields for WordAsGetManyWordResponse
        criteriaQuery.select(
            criteriaBuilder.construct(
                WordAsGetManyWordResponse::class.java,
                root.get<UUID>("id"),
                root.get<Int>("points"),
                root.get<String>("origin"),
                root.get<String>("translation"),
                root.get<Boolean>("isBookmarked"),
                root.get<WordType>("type"),
                root.get<WordExtraMark?>("extraMark"),
                root.get<LanguageName>("translatedFrom"),
                root.get<LanguageName>("translatedTo"),
//                root.get<Bank?>("bank"),
                root.get<UUID?>("bankId"),
                root.get<Instant>("createdAt"),
                root.get<Instant>("updatedAt")
            )
        )

        // Execute query with pagination if needed
        return entityManager.createQuery(criteriaQuery)
            .setFirstResult(page * perPage)
            .setMaxResults(perPage)
            .resultList
    }
}