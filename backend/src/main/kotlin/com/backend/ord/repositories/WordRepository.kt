package com.backend.ord.repositories

import com.backend.ord.domain.entities.Word
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WordRepository : UserResourceRepository<Word> {
    @Query("SELECT w FROM Word w WHERE w.user.id = :userId AND (lower(w.origin) LIKE lower(concat('%', :phrase, '%')) OR lower(w.translation) LIKE lower(concat('%', :phrase, '%')))")
    override fun findAllForUserBySearchingPhrase(userId: UUID, phrase: String): List<Word>
}