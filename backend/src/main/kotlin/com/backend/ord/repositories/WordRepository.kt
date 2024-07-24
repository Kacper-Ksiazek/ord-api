package com.backend.ord.repositories

import com.backend.ord.domain.entities.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface WordRepository : JpaRepository<Word, UUID> {
    @Query("SELECT w FROM Word w WHERE w.user.id = :userId")
    fun findAllByUserId(userId: UUID): List<Word>
}