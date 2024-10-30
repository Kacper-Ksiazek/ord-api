package com.backend.ord.repositories.impl

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.repositories.WordRepositoryCustomMethods
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
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
    ): List<Word?>? {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Word::class.java)
        val root = criteriaQuery.from(Word::class.java)

        val predicates = mutableListOf<Predicate>()

        // Mandatory predicate
        predicates.add(criteriaBuilder.equal(root.get<LanguageName>("translatedFrom"), language))

//        predicates.add(criteriaBuilder.equal(root.get<UUID>("userId"), user.id))

        // Optional predicates
        wordType?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordType>("type"), it))
        }
//        searchingPhrase?.let {
//            predicates.add(criteriaBuilder.like(root.get("phrase"), "%$it%"))
//        }
        wordExtraMark?.let {
            predicates.add(criteriaBuilder.equal(root.get<WordExtraMark>("extraMark"), it))
        }
        if (bookmarkedOnly == true) {
            predicates.add(criteriaBuilder.isTrue(root.get<Boolean>("isBookmarked")))
        }
//        banksIds?.takeIf { it.isNotEmpty() }?.let {
//            predicates.add(root.get<UUID>("bankId").`in`(it))
//        }

        // Apply predicates
        criteriaQuery.where(*predicates.toTypedArray())

        // Sorting
        // TODO: Implement column names mapping to entity fields and then implement sorting
//        val order = if (sortDirection == SortDirection.DESC) {
//            criteriaBuilder.desc(root.get<Any>(sortBy.name))
//        } else {
//            criteriaBuilder.asc(root.get<Any>(sortBy.name))
//        }
//        criteriaQuery.orderBy(order)

        // Execute query with pagination
        return entityManager.createQuery(criteriaQuery)
            .setFirstResult(page * perPage)
            .setMaxResults(perPage)
            .resultList
    }
}