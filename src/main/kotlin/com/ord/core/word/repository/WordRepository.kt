package com.ord.core.word.repository

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.model.WordEntity
import com.ord.shared.domain.projections.CountingSummaryProjection
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WordRepository :
    UserResourceRepository<WordEntity>,
    WordRepositoryCustomMethods {
    // ------
    // READ
    // ------

    @Query("SELECT w.origin FROM WordEntity w WHERE w.translatedFrom = :language ORDER BY w.createdAt DESC")
    fun findNOfLatestWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM WordEntity w WHERE w.translatedFrom = :language ORDER BY w.points DESC")
    fun findNOfMostDifficultWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM WordEntity w WHERE w.translatedFrom = :language AND w.bank.id IN :banksIds")
    fun findAllWordsFromBanks(language: LanguageName, banksIds: List<UUID>): List<String>

    @Query("SELECT w FROM WordEntity w WHERE w.translatedFrom = :language AND w.origin IN :origins AND w.user.id = :userId")
    fun findAllWordByTheirOrigins(origins: Set<String>, language: LanguageName, userId: UUID): List<WordEntity>

    // ------
    // AGGREGATE
    // ------

    @Query(
        value = """
            SELECT * FROM count_words_by_field(
                'created_at', 
                cast(:language as text), 
                :userId
            )
        """,
        nativeQuery = true
    )
    fun countCreated(language: LanguageName, userId: UUID): CountingSummaryProjection

    @Query(
        value = """
            SELECT * FROM count_words_by_field(
                'completed_at', 
                cast(:language as text), 
                :userId
            )
        """,
        nativeQuery = true
    )
    fun countCompleted(language: LanguageName, userId: UUID): CountingSummaryProjection

    // ------
    // UPDATE
    // ------

    @Modifying
    @Query("UPDATE WordEntity w SET w.bank.id = :bankId WHERE w.id = :wordId AND w.user.id = :userId")
    fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Int

    @Modifying
    @Query("UPDATE WordEntity w SET w.bank.id = :bankId WHERE w.id IN :wordIds AND w.user.id = :userId")
    fun changeBankForMultipleWords(bankId: UUID?, wordIds: List<UUID>, userId: UUID): Int
}